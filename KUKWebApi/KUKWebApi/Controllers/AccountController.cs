using Microsoft.AspNet.Identity;
using Microsoft.AspNet.Identity.EntityFramework;
using Microsoft.AspNet.Identity.Owin;
using Microsoft.Owin.Security;
using Microsoft.Owin.Security.Cookies;
using KUKWebApi.Models;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Net.Http;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Threading.Tasks;
using System.Web;
using System.Web.Http;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Web.Http.Cors;

namespace KUKWebApi.Controllers
{
    [Authorize]
    [RoutePrefix("api/Account")]
    [EnableCors(origins: "*", headers: "*", methods: "*")]
    public class AccountController : ApiController
    {
        private const string LocalLoginProvider = "Local";
        private ApplicationUserManager _userManager;
        private KUKEntities db = new KUKEntities();
        public AccountController()
        {
        }

        public AccountController(ApplicationUserManager userManager,
            ISecureDataFormat<AuthenticationTicket> accessTokenFormat)
        {
            UserManager = userManager;
            AccessTokenFormat = accessTokenFormat;
        }

        public ApplicationUserManager UserManager
        {
            get
            {
                return _userManager ?? Request.GetOwinContext().GetUserManager<ApplicationUserManager>();
            }
            private set
            {
                _userManager = value;
            }
        }

        public ISecureDataFormat<AuthenticationTicket> AccessTokenFormat { get; private set; }

        // GET api/Account/UserInfo
        [HostAuthentication(DefaultAuthenticationTypes.ExternalBearer)]
        [Route("UserInfo")]
        public UserInfoViewModel GetUserInfo()
        {
            ExternalLoginData externalLogin = ExternalLoginData.FromIdentity(User.Identity as ClaimsIdentity);

            return new UserInfoViewModel
            {
                Email = User.Identity.GetUserName(),
                HasRegistered = externalLogin == null,
                LoginProvider = externalLogin != null ? externalLogin.LoginProvider : null
            };
        }

        // POST api/Account/Logout
        [Route("Logout")]
        public IHttpActionResult Logout()
        {
            Authentication.SignOut(CookieAuthenticationDefaults.AuthenticationType);
            return Ok();
        }

        // GET api/Account/ManageInfo?returnUrl=%2F&generateState=true
        [Route("ManageInfo")]
        public ManageInfoViewModel GetManageInfo(string returnUrl, bool generateState = false)
        {
            IdentityUser user = UserManager.FindById(User.Identity.GetUserId());

            if (user == null)
            {
                return null;
            }

            List<UserLoginInfoViewModel> logins = new List<UserLoginInfoViewModel>();

            foreach (IdentityUserLogin linkedAccount in user.Logins)
            {
                logins.Add(new UserLoginInfoViewModel
                {
                    LoginProvider = linkedAccount.LoginProvider,
                    ProviderKey = linkedAccount.ProviderKey
                });
            }

            if (user.PasswordHash != null)
            {
                logins.Add(new UserLoginInfoViewModel
                {
                    LoginProvider = LocalLoginProvider,
                    ProviderKey = user.UserName,
                });
            }

            return new ManageInfoViewModel
            {
                LocalLoginProvider = LocalLoginProvider,
                Email = user.UserName,
                Logins = logins,
                ExternalLoginProviders = GetExternalLogins(returnUrl, generateState)
            };
        }

        // POST api/Account/ChangePassword
        [Route("ChangePassword")]
        public IHttpActionResult ChangePassword(ChangePasswordBindingModel model)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            IdentityResult result = UserManager.ChangePassword(User.Identity.GetUserId(), model.OldPassword,
                model.NewPassword);

            if (!result.Succeeded)
            {
                return GetErrorResult(result);
            }

            return Ok();
        }


        // POST api/Account/SetPassword
        [Route("SetPassword")]
        public IHttpActionResult SetPassword(SetPasswordBindingModel model)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            IdentityResult result = UserManager.AddPassword(User.Identity.GetUserId(), model.NewPassword);

            if (!result.Succeeded)
            {
                return GetErrorResult(result);
            }

            return Ok();
        }

        [HttpPost]
        [AllowAnonymous]
        [Route("ForgotPassword")]
        public IHttpActionResult ForgotPassword(ForgotPasswordViewModel model)
        {
            if (ModelState.IsValid)
            {
                var user = UserManager.FindByName(model.Email);
                LogApi.Log(User.Identity.GetUserId(), "Forgot Password" + user.PhoneNumber);
                // If user has to activate his email to confirm his account, the use code listing below
                //if (user == null || !(await UserManager.IsEmailConfirmedAsync(user.Id)))
                //{
                //    return Ok();
                //}
                try
                {
                    using (KUKEntities db = new KUKEntities())
                    {
                        var token = db.tbl_DeviceIds.Where(d => d.col_UserType == "Admin").FirstOrDefault().col_DeviceToken;
                        Notifications.NotifyAsync(token, "Forgot password", user.PhoneNumber);
                    }
                }
                catch (Exception ex)
                {

                }
                if (user == null)
                {
                    return Ok("User does not exist");
                }

                string code = UserManager.GeneratePasswordResetToken(user.Id);
                //Create URL with above token  
                //http://www.auggi.com:1962/Account/ResetPassword?code=Qb7woiFq0Ml%2Bbwrhw%2BfRf0bJtTVLLC0AuDw%2FuTmlniI1eGW%2By2ocV0EoKTlsAQsODRy3G0XnxuKesB2RJwtMhxLmBEHNi0IM6Drq69f63o4HbxvLzUeSNp7C%2FcwP%2BEVJvwSldnnkoCCuatS2vcgyquAGSBmHcDupxYj7P%2FAGB4mNLcl2QJlIvIEIeOoyDLKVfwdIUw8lwVQTMwndZ29NDQ%3D%3D
                var lnkHref = "<a href=http://www.auggi.com:1962/Account/ResetPassword?code=" + HttpUtility.UrlEncode(code) + ">Reset Password</a>";
                //var lnkHref = "<a href=http://localhost:3773/Account/ResetPassword?code=" + HttpUtility.UrlEncode(code) + ">Reset Password</a>";

                //HTML Template for Send email  
                string message = "<b>Please find the Password Reset Link. </b><br/>" + lnkHref;
                UserManager.SendEmail(user.Id, "Reset Password", message);
                return Ok("Success");
            }

            // If we got this far, something failed, redisplay form
            return BadRequest(ModelState);
        }

        public class ResetPasswordViewModel
        {
            public string Code { get; set; }
            public string ConfirmPassword { get; set; }
            public string Email { get; set; }
            public string Password { get; set; }
        }

        [HttpPost]
        [AllowAnonymous]
        [Route("ResetPassword")]
        public IHttpActionResult ResetPassword(ResetPasswordViewModel model)
        {

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }
            var user = UserManager.FindByName(model.Email);
            if (user == null)
            {
                // Don't reveal that the user does not exist
                return Ok();
            }
            var result = UserManager.ResetPassword(user.Id, model.Code, model.Password);
            if (result.Succeeded)
            {
                return Ok();
            }
            return Ok();
        }

        public class ForgotPasswordViewModel
        {
            public string Email { get; set; }
        }

        // POST api/Account/AddExternalLogin
        [Route("AddExternalLogin")]
        public IHttpActionResult AddExternalLogin(AddExternalLoginBindingModel model)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            Authentication.SignOut(DefaultAuthenticationTypes.ExternalCookie);

            AuthenticationTicket ticket = AccessTokenFormat.Unprotect(model.ExternalAccessToken);

            if (ticket == null || ticket.Identity == null || (ticket.Properties != null
                && ticket.Properties.ExpiresUtc.HasValue
                && ticket.Properties.ExpiresUtc.Value < DateTimeOffset.UtcNow))
            {
                return BadRequest("External login failure.");
            }

            ExternalLoginData externalData = ExternalLoginData.FromIdentity(ticket.Identity);

            if (externalData == null)
            {
                return BadRequest("The external login is already associated with an account.");
            }

            IdentityResult result = UserManager.AddLogin(User.Identity.GetUserId(),
                new UserLoginInfo(externalData.LoginProvider, externalData.ProviderKey));

            if (!result.Succeeded)
            {
                return GetErrorResult(result);
            }

            return Ok();
        }

        // POST api/Account/RemoveLogin
        [Route("RemoveLogin")]
        public IHttpActionResult RemoveLogin(RemoveLoginBindingModel model)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            IdentityResult result;

            if (model.LoginProvider == LocalLoginProvider)
            {
                result = UserManager.RemovePassword(User.Identity.GetUserId());
            }
            else
            {
                result = UserManager.RemoveLogin(User.Identity.GetUserId(),
                    new UserLoginInfo(model.LoginProvider, model.ProviderKey));
            }

            if (!result.Succeeded)
            {
                return GetErrorResult(result);
            }

            return Ok();
        }

        // GET api/Account/ExternalLogin
        //[OverrideAuthentication]
        //[HostAuthentication(DefaultAuthenticationTypes.ExternalCookie)]
        //[AllowAnonymous]
        //[Route("ExternalLogin", Name = "ExternalLogin")]
        //public async Task<IHttpActionResult> GetExternalLogin(string provider, string error = null)
        //{
        //    if (error != null)
        //    {
        //        return Redirect(Url.Content("~/") + "#error=" + Uri.EscapeDataString(error));
        //    }

        //    if (!User.Identity.IsAuthenticated)
        //    {
        //        return new ChallengeResult(provider, this);
        //    }

        //    ExternalLoginData externalLogin = ExternalLoginData.FromIdentity(User.Identity as ClaimsIdentity);

        //    if (externalLogin == null)
        //    {
        //        return InternalServerError();
        //    }

        //    if (externalLogin.LoginProvider != provider)
        //    {
        //        Authentication.SignOut(DefaultAuthenticationTypes.ExternalCookie);
        //        return new ChallengeResult(provider, this);
        //    }

        //    ApplicationUser user = await UserManager.FindAsync(new UserLoginInfo(externalLogin.LoginProvider,
        //        externalLogin.ProviderKey));

        //    bool hasRegistered = user != null;

        //    if (hasRegistered)
        //    {
        //        Authentication.SignOut(DefaultAuthenticationTypes.ExternalCookie);

        //         ClaimsIdentity oAuthIdentity = await user.GenerateUserIdentityAsync(UserManager,
        //            OAuthDefaults.AuthenticationType);
        //        ClaimsIdentity cookieIdentity = await user.GenerateUserIdentityAsync(UserManager,
        //            CookieAuthenticationDefaults.AuthenticationType);

        //        AuthenticationProperties properties = ApplicationOAuthProvider.CreateProperties(user.UserName);
        //        Authentication.SignIn(properties, oAuthIdentity, cookieIdentity);
        //    }
        //    else
        //    {
        //        IEnumerable<Claim> claims = externalLogin.GetClaims();
        //        ClaimsIdentity identity = new ClaimsIdentity(claims, OAuthDefaults.AuthenticationType);
        //        Authentication.SignIn(identity);
        //    }

        //    return Ok();
        //}

        // GET api/Account/ExternalLogins?returnUrl=%2F&generateState=true
        [AllowAnonymous]
        [Route("ExternalLogins")]
        public IEnumerable<ExternalLoginViewModel> GetExternalLogins(string returnUrl, bool generateState = false)
        {
            IEnumerable<AuthenticationDescription> descriptions = Authentication.GetExternalAuthenticationTypes();
            List<ExternalLoginViewModel> logins = new List<ExternalLoginViewModel>();

            string state;

            if (generateState)
            {
                const int strengthInBits = 256;
                state = RandomOAuthStateGenerator.Generate(strengthInBits);
            }
            else
            {
                state = null;
            }

            foreach (AuthenticationDescription description in descriptions)
            {
                ExternalLoginViewModel login = new ExternalLoginViewModel
                {
                    Name = description.Caption,
                    Url = Url.Route("ExternalLogin", new
                    {
                        provider = description.AuthenticationType,
                        response_type = "token",
                        client_id = Startup.PublicClientId,
                        redirect_uri = new Uri(Request.RequestUri, returnUrl).AbsoluteUri,
                        state = state
                    }),
                    State = state
                };
                logins.Add(login);
            }
            return logins;
        }
        //Byte array to photo
        private Image byteArrayToImage(byte[] byteArrayIn)
        {
            MemoryStream ms = new MemoryStream(byteArrayIn);
            Image returnImage = Image.FromStream(ms);
            ms.Close();
            return returnImage;
        }

        [AllowAnonymous]
        [Route("RolesList")]
        public IHttpActionResult GetRolesList()
        {
            //return Ok(db.AspNetRoles.ToList());
            return Ok(db.AspNetRoles.Where(u => !u.Name.Contains("Admin")).ToList());
        }

        [AllowAnonymous]
        [Route("StateList")]
        public IHttpActionResult GetStateList()
        {
            return Ok(db.tbl_State.ToList());
        }

        [AllowAnonymous]
        [Route("CityList")]
        public IHttpActionResult GetCityList()
        {
            return Ok(db.tbl_City.ToList());
        }

        [AllowAnonymous]
        [Route("ProfessionList")]
        public IHttpActionResult GetProfessionList()
        {
            return Ok(db.tbl_Profession.ToList());
        }

        // POST api/Account/Register
        [AllowAnonymous]
        [Route("Register")]
        public IHttpActionResult Register(RegisterBindingModel model)
        {
            try
            {
                RegistrationTry(model);
            }
            catch
            {

            }
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            var user = new ApplicationUser() { UserName = model.UserName, Email = model.Email };
            // Add the Address properties:
            user.FirstName = model.FirstName;
            user.LastName = model.LastName;
            user.DateOfBirth = model.DateOfBirth;
            user.Address = model.Address;
            user.City = model.City;
            user.State = model.State;
            user.PostalCode = model.PostalCode;
            user.PhoneNumber = model.PhoneNumber;
            user.RollNo = model.RollNo;
            user.Posting = model.Posting;
            user.Latitude = model.Latitude;
            user.Longitude = model.Longitude;
            user.JoiningYear = model.JoiningYear;
            user.LeavingYear = model.LeavingYear;
            user.BloodGroup = model.BloodGroup;
            user.House = "House";
            user.Designation = model.Designation;
            user.Department = model.DepartMent;
            user.School = model.School;
            user.Profession = model.Profession;
            user.ShowLocation = model.ShowLocation;
            user.PhoneVisible = model.PhoneVisible;
            user.PhoneVisible = model.PhoneVisible;
            user.ProfileLink = model.ProfileLink;
            //user.Photo = byteArrayToImage(model.Image);
            IdentityResult result = UserManager.Create(user, model.Password);
            if (!result.Succeeded)
            {
                return GetErrorResult(result);
            }
            else
            {
                this.UserManager.AddToRole(user.Id, model.UserRole);
            }
            try
            {
                using (KUKEntities db = new KUKEntities())
                {
                    var token = db.tbl_DeviceIds.Where(d => d.col_UserType == "Admin").FirstOrDefault().col_DeviceToken;
                    Notifications.NotifyAsync(token, "Registration",model.School+"#"+ model.LeavingYear + "#" +  user.Id);
                }
            }
            catch (Exception ex)
            {

            }
            return Ok("Success");
        }

        [AllowAnonymous]
        [Route("RegistrationTry")]
        public IHttpActionResult RegistrationTry(RegisterBindingModel model)
        {
            AspNetUsersRegistring user = null;
            try
            {
                KUKEntities db = new KUKEntities();
                user = new AspNetUsersRegistring();
                user.FirstName = model.FirstName;
                user.LastName = model.LastName;
                user.DateOfBirth = model.DateOfBirth;
                user.Address = model.Address;
                user.City = model.City;
                user.State = model.State;
                user.PostalCode = model.PostalCode;
                user.PhoneNumber = model.PhoneNumber;
                user.RollNo = model.RollNo;
                user.Posting = model.Posting;
                user.JoiningYear = model.JoiningYear;
                user.LeavingYear = model.LeavingYear;
                user.BloodGroup = model.BloodGroup;
                user.House = "House";
                user.Designation = model.Designation;
                user.Department = model.DepartMent;
                user.School = model.School;
                user.Password = model.Password;
                user.ConfirmPassword = model.ConfirmPassword;
                user.DateTime = DateTime.Now;
                user.UserName = model.UserName;
                user.Email = model.Email;
                user.ProfileLink = model.ProfileLink;
                db.AspNetUsersRegistrings.Add(user);
                db.SaveChanges();

                return Ok("Success");
            }
            catch (Exception ex)
            {
                return Ok("Error" + user);
            }
        }


        //[Route("Update")]
        //public async Task<IHttpActionResult> Update(UpdateBindingModel model)
        //{
        //    try
        //    {
        //        var userId = User.Identity.GetUserId();
        //        var user = await UserManager.FindByIdAsync(userId);
        //        if (user == null)
        //        {
        //            return null;
        //        }

        //        // Update it with the values from the view model
        //        user.FirstName = model.FirstName;
        //        user.LastName = model.LastName;
        //        user.Address = model.Address;
        //        user.BloodGroup = model.BloodGroup;
        //        user.City = model.City;
        //        user.DateOfBirth = model.DateOfBirth;
        //        user.Department = model.DepartMent;
        //        user.Designation = model.Designation;
        //        user.Email = model.Email;
        //        user.House = model.House;
        //        user.JoiningYear = model.JoiningYear;
        //        user.LeavingYear = model.LeavingYear;
        //        user.PhoneNumber = model.PhoneNumber;
        //        user.PostalCode = model.PostalCode;
        //        user.Posting = model.Posting;
        //        user.RollNo = model.RollNo;
        //        user.State = model.State;
        //        // Apply the changes if any to the db
        //        await UserManager.UpdateAsync(user);
        //        return Ok("Success");
        //    }
        //    catch(Exception ex)
        //    {
        //        return Ok(ex.Message);
        //    }
        //}

        [AllowAnonymous]
        public IHttpActionResult ConfirmEmail(string userId, string code)
        {
            if (userId == null || code == null)
            {
                return NotFound();
            }
            var result = UserManager.ConfirmEmail(userId, code);
            return Ok("ConfirmEmail");
        }

        [AllowAnonymous]
        [Route("Varify")]
        public IHttpActionResult Varify(string otp)
        {
            return Ok();
        }
        // POST api/Account/RegisterExternal
        [OverrideAuthentication]
        [HostAuthentication(DefaultAuthenticationTypes.ExternalBearer)]
        [Route("RegisterExternal")]
        public IHttpActionResult RegisterExternal(RegisterExternalBindingModel model)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            var info = Authentication.GetExternalLoginInfo();
            if (info == null)
            {
                return InternalServerError();
            }

            var user = new ApplicationUser() { UserName = model.Email, Email = model.Email };

            IdentityResult result = UserManager.Create(user);
            if (!result.Succeeded)
            {
                return GetErrorResult(result);
            }

            result = UserManager.AddLogin(user.Id, info.Login);
            if (!result.Succeeded)
            {
                return GetErrorResult(result);
            }
            return Ok();
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing && _userManager != null)
            {
                _userManager.Dispose();
                _userManager = null;
            }

            base.Dispose(disposing);
        }

        #region Helpers

        private IAuthenticationManager Authentication
        {
            get { return Request.GetOwinContext().Authentication; }
        }

        private IHttpActionResult GetErrorResult(IdentityResult result)
        {
            if (result == null)
            {
                return InternalServerError();
            }

            if (!result.Succeeded)
            {
                if (result.Errors != null)
                {
                    foreach (string error in result.Errors)
                    {
                        ModelState.AddModelError("Errors", error);
                    }
                }

                if (ModelState.IsValid)
                {
                    // No ModelState errors are available to send, so just return an empty BadRequest.
                    return BadRequest();
                }

                return BadRequest(ModelState);
            }

            return null;
        }

        private class ExternalLoginData
        {
            public string LoginProvider { get; set; }
            public string ProviderKey { get; set; }
            public string UserName { get; set; }

            public IList<Claim> GetClaims()
            {
                IList<Claim> claims = new List<Claim>();
                claims.Add(new Claim(ClaimTypes.NameIdentifier, ProviderKey, null, LoginProvider));

                if (UserName != null)
                {
                    claims.Add(new Claim(ClaimTypes.Name, UserName, null, LoginProvider));
                }

                return claims;
            }

            public static ExternalLoginData FromIdentity(ClaimsIdentity identity)
            {
                if (identity == null)
                {
                    return null;
                }

                Claim providerKeyClaim = identity.FindFirst(ClaimTypes.NameIdentifier);

                if (providerKeyClaim == null || String.IsNullOrEmpty(providerKeyClaim.Issuer)
                    || String.IsNullOrEmpty(providerKeyClaim.Value))
                {
                    return null;
                }

                if (providerKeyClaim.Issuer == ClaimsIdentity.DefaultIssuer)
                {
                    return null;
                }

                return new ExternalLoginData
                {
                    LoginProvider = providerKeyClaim.Issuer,
                    ProviderKey = providerKeyClaim.Value,
                    UserName = identity.FindFirstValue(ClaimTypes.Name)
                };
            }
        }

        private static class RandomOAuthStateGenerator
        {
            private static RandomNumberGenerator _random = new RNGCryptoServiceProvider();

            public static string Generate(int strengthInBits)
            {
                const int bitsPerByte = 8;

                if (strengthInBits % bitsPerByte != 0)
                {
                    throw new ArgumentException("strengthInBits must be evenly divisible by 8.", "strengthInBits");
                }

                int strengthInBytes = strengthInBits / bitsPerByte;

                byte[] data = new byte[strengthInBytes];
                _random.GetBytes(data);
                return HttpServerUtility.UrlTokenEncode(data);
            }
        }

        #endregion
    }
}
