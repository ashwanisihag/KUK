using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using System.Web;
using System.Web.Http;
using System.Web.Http.Description;
using Microsoft.AspNet.Identity;
using KUKWebApi.Models;
using Newtonsoft.Json;
using System.Device.Location;
using System.Text;
using System.Web.Http.Cors;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.Data.Entity.SqlServer;

namespace KUKWebApi.Controllers
{
    [Authorize]
    [EnableCors(origins: "*", headers: "*", methods: "*")]
    public class AspNetUsersController : ApiController
    {
        private KUKEntities db = new KUKEntities();
        // GET: api/AspNetUsers
        [HttpGet]
        [ActionName("GetMembers")]
        public IEnumerable<AspNetUser> GetAspNetUsers([FromUri]PagingParameterModel pagingparametermodel, string leavingYear)
        {

            IEnumerable<AspNetUser> source = null;
            var adminId = db.tbl_DeviceIds.Where(d => d.col_UserType == "Admin").FirstOrDefault().col_UserID;
            LogApi.Log(User.Identity.GetUserId(), "Login " + User.Identity.GetUserName());
            try
            {
                if (adminId != User.Identity.GetUserId())
                {
                    var token = db.tbl_DeviceIds.Where(d => d.col_UserType == "Admin").FirstOrDefault().col_DeviceToken;
                    Notifications.NotifyAsync(token, "Login", User.Identity.GetUserName() + "#" + User.Identity.GetUserId() + "#" + User.Identity.GetUserId());
                }
            }

            catch (Exception ex)
            {

            }
            if (!string.IsNullOrEmpty(leavingYear))
            {
                // Return List of Customer  
                source = (from users in db.AspNetUsers.Where(u => u.LeavingYear.Year.ToString() == leavingYear)
                          select users).AsQueryable();
            }
            else
            {
                // Return List of Customer  
                source = (from users in db.AspNetUsers
                          select users).AsQueryable();
            }
            source = source.OrderBy(u => u.School);
            // Get's No of Rows Count   
            int count = source.Count();

            // Parameter is passed from Query string if it is null then it default Value will be pageNumber:1  
            int CurrentPage = pagingparametermodel.pageNumber;

            // Parameter is passed from Query string if it is null then it default Value will be pageSize:20  
            int PageSize = pagingparametermodel.pageSize;

            // Display TotalCount to Records to User  
            int TotalCount = count;

            // Calculating Totalpage by Dividing (No of Records / Pagesize)  
            int TotalPages = (int)Math.Ceiling(count / (double)PageSize);

            // Returns List of Customer after applying Paging   
            var items = source.Skip((CurrentPage - 1) * PageSize).Take(PageSize).ToList();

            // if CurrentPage is greater than 1 means it has previousPage  
            var previousPage = CurrentPage > 1 ? "Yes" : "No";

            // if TotalPages is greater than CurrentPage means it has nextPage  
            var nextPage = CurrentPage < TotalPages ? "Yes" : "No";

            // Object which we are going to send in header   
            var paginationMetadata = new
            {
                totalCount = TotalCount,
                pageSize = PageSize,
                currentPage = CurrentPage,
                totalPages = TotalPages,
                previousPage,
                nextPage
            };

            // Setting Header  
            HttpContext.Current.Response.Headers.Add("Paging-Headers", JsonConvert.SerializeObject(paginationMetadata));
            // Returing List of Customers Collections  
            return items;
        }

        [HttpGet]
        [ActionName("GetOnlineMembers")]
        public IHttpActionResult GetOnlineMembers()
        {
            var id = User.Identity.GetUserId();
            var onlineMembers = (from d in db.tbl_DeviceIds
                             join u in db.AspNetUsers
                             on d.col_UserID equals u.Id
                             where SqlFunctions.DateDiff("second", d.col_DateTime, DateTime.Now) <= 600
                             select u).Where(u => u.Id != id).ToList();

            //onlineMembers = onlineMembers.OrderBy(u => u.School).ToList();
            if (onlineMembers.Count > 0)
            {
                return  Ok(onlineMembers);
            }
              else
                {
                    return Ok("No records found");
                }
        }

        [HttpGet]
        [ActionName("GetMembersXamarin")]
        public IEnumerable<AspNetUser> GetMembersXamarin(string leavingYear)
        {

            IEnumerable<AspNetUser> source = null;
            var adminId = db.tbl_DeviceIds.Where(d => d.col_UserType == "Admin").FirstOrDefault().col_UserID;
            LogApi.Log(User.Identity.GetUserId(), "Login " + User.Identity.GetUserName());
            try
            {
                if (adminId != User.Identity.GetUserId())
                {
                    var token = db.tbl_DeviceIds.Where(d => d.col_UserType == "Admin").FirstOrDefault().col_DeviceToken;
                    Notifications.NotifyAsync(token, "Login", User.Identity.GetUserName() + "#" + User.Identity.GetUserId() + "#" + User.Identity.GetUserId());
                }
            }

            catch (Exception ex)
            {

            }
            if (!string.IsNullOrEmpty(leavingYear))
            {
                // Return List of Customer  
                source = (from users in db.AspNetUsers.Where(u => u.LeavingYear.Year.ToString() == leavingYear)
                          select users).AsQueryable();
            }
            else
            {
                // Return List of Customer  
                source = (from users in db.AspNetUsers
                          select users).AsQueryable();
            }
            source = source.OrderBy(u => u.FirstName);
            // Get's No of Rows Count   


            // Returing List of Customers Collections  
            return source;
        }

        [HttpGet]
        [ActionName("GetDuplicateMembers")]
        public IEnumerable<AspNetUser> GetDuplicateMembers()
        {

            IEnumerable<AspNetUser> source = null;

            source = db.AspNetUsers
                      .SqlQuery("WITH TempEmp(Id, FirstName, LastName, Email, EmailConfirmed, PasswordHash, SecurityStamp, PhoneNumber, PhoneNumberConfirmed, TwoFactorEnabled, LockoutEndDateUtc, LockoutEnabled, AccessFailedCount, UserName, Address, DateOfBirth, City, State, PostalCode, RollNo, JoiningYear, LeavingYear, Designation, Posting, BloodGroup, Latitude, Longitude, House, Department, LocationTime, School, PhoneVisible, ShowLocation, Profession, ProfileLink, duplicateRecCount) AS (SELECT Id, FirstName, LastName, Email, EmailConfirmed, PasswordHash, SecurityStamp, PhoneNumber, PhoneNumberConfirmed, TwoFactorEnabled, LockoutEndDateUtc, LockoutEnabled, AccessFailedCount, UserName, Address, DateOfBirth, City, State, PostalCode, RollNo, JoiningYear, LeavingYear, Designation, Posting, BloodGroup, Latitude, Longitude, House, Department, LocationTime, School, PhoneVisible, ShowLocation, Profession, ProfileLink, ROW_NUMBER() OVER(PARTITION by email ORDER BY email) AS duplicateRecCount FROM[KUK].[dbo].[AspNetUsers]) select * from TempEmp WHERE duplicateRecCount > 1").ToList<AspNetUser>();



            source = source.OrderBy(u => u.FirstName);
            // Get's No of Rows Count   


            // Returing List of Customers Collections  
            return source;
        }


        [HttpGet]
        [ActionName("GetBirthdayMembers")]
        public IEnumerable<AspNetUser> GetBirthdayMembers()
        {
            LogApi.Log(User.Identity.GetUserId(), "GetBirthdayMembers " + User.Identity.GetUserName());

            IEnumerable<AspNetUser> source = null;
            // Return List of Customer  
            source = (from users in db.AspNetUsers
                      select users).Where(u => u.DateOfBirth.Day == DateTime.Now.Day && u.DateOfBirth.Month == DateTime.Now.Month).AsQueryable();

            return source;
        }


        //[HttpGet]
        //[ActionName("RequestFiles")]
        //[Route("Register")]
        //public List<int> Get_RequestFiles()
        //{
        //    var data = from i in db.tbl_Files
        //               select i.col_RequestFileID;
        //    return data.ToList();
        //}
        public class UserLocation
        {
            public double Latitude { get; set; }
            public double Longitude { get; set; }
            public double CityLatitude { get; set; }
            public double CityLongitude { get; set; }
            public int Radius { get; set; }
            public string Filter { get; set; }
            public bool CitySearch { get; set; }
        }


        public class UserSearch
        {
            public string FirstName { get; set; }
            public string LastName { get; set; }
            public string Profession { get; set; }
            public string ProfileLink { get; set; }
            public string Batch { get; set; }
            public string RollNo { get; set; }
            public string School { get; set; }
            public string Department { get; set; }
            public string Designation { get; set; }
            public string City { get; set; }
        }

        public class NearbyUsers
        {
            public string Id { get; set; }
            public string School { get; set; }
            public string FirstName { get; set; }
            public string LastName { get; set; }
            public string Address { get; set; }
            public string City { get; set; }
            public string State { get; set; }
            public string PostalCode { get; set; }
            public double Distance { get; set; }
            public System.DateTime JoiningYear { get; set; }
            public System.DateTime LeavingYear { get; set; }
            public int RollNo { get; set; }
            public string Designation { get; set; }
            public string Posting { get; set; }
            public string BloodGroup { get; set; }
            public string House { get; set; }
            public string Department { get; set; }
            public string PhoneNumber { get; set; }
            public string LocationDaysOld { get; set; }
            public string Profession { get; set; }
            public string ProfileLink { get; set; }
        }

        public class SearchedUsers
        {
            public string Id { get; set; }
            public string School { get; set; }
            public string FirstName { get; set; }
            public string LastName { get; set; }
            public string Address { get; set; }
            public string City { get; set; }
            public string State { get; set; }
            public string PostalCode { get; set; }
            public System.DateTime JoiningYear { get; set; }
            public System.DateTime LeavingYear { get; set; }
            public int RollNo { get; set; }
            public string Designation { get; set; }
            public string Posting { get; set; }
            public string BloodGroup { get; set; }
            public string House { get; set; }
            public string Department { get; set; }
            public string PhoneNumber { get; set; }
            public string Profession { get; set; }
            public string ProfileLink { get; set; }
        }
        [HttpPut]
        [ActionName("UpdateUserLocation")]
        public IHttpActionResult Puttbl_UserLocation(UserLocation loc)
        {
            LogApi.Log(User.Identity.GetUserId(), "UpdateUserLocation from " + loc.Filter + " " + User.Identity.GetUserName());

            List<AspNetUser> list_AspNetUser = null;
            if (loc.Radius == 0)
            {
                loc.Radius = 50000;
            }
            else
            {
                loc.Radius = loc.Radius * 1000;
            }
            try
            {
                //Get employee
                var userId = User.Identity.GetUserId();
                AspNetUser AspNetUser = db.AspNetUsers.Where(e => e.Id == userId).FirstOrDefault();
                if (loc.Filter == "Batch")
                {
                    list_AspNetUser = db.AspNetUsers.Where(u => u.LeavingYear.Year == AspNetUser.LeavingYear.Year && u.School == AspNetUser.School).ToList();
                }
                else if (loc.Filter == "Branch")
                {
                    list_AspNetUser = db.AspNetUsers.ToList();
                }
                else if (loc.Filter == "School")
                {
                    list_AspNetUser = db.AspNetUsers.Where(u => u.School == AspNetUser.School).ToList();
                }
                else
                {
                    list_AspNetUser = db.AspNetUsers.ToList();
                }
                List<NearbyUsers> nearbyUserList = new List<NearbyUsers>();
                NearbyUsers nearbyUsers;
                foreach (var x in list_AspNetUser.Where(u => u.Id != userId))
                {
                    GeoCoordinate eCoord = null;
                    if (loc.CitySearch)
                    {
                        eCoord = new GeoCoordinate(loc.CityLatitude, loc.CityLongitude);
                    }
                    else
                    {
                        eCoord = new GeoCoordinate((double)AspNetUser.Latitude, (double)AspNetUser.Longitude);
                    }
                    var sCoord = new GeoCoordinate(Convert.ToDouble(x.Latitude), Convert.ToDouble(x.Longitude));
                    double distance = sCoord.GetDistanceTo(eCoord);
                    if (distance < loc.Radius && distance > 0 && sCoord.Latitude != 0 && sCoord.Longitude != 0)
                    {
                        nearbyUsers = new NearbyUsers();
                        nearbyUsers.Id = x.Id;
                        nearbyUsers.FirstName = x.FirstName;
                        nearbyUsers.LastName = x.LastName;
                        nearbyUsers.Address = x.Address;
                        nearbyUsers.BloodGroup = x.BloodGroup;
                        nearbyUsers.City = x.City;
                        nearbyUsers.Department = x.Department;
                        nearbyUsers.School = x.School;
                        nearbyUsers.Designation = x.Designation;
                        nearbyUsers.House = x.House;
                        nearbyUsers.PostalCode = x.PostalCode;
                        nearbyUsers.Posting = x.Posting;
                        //nearbyUsers.RollNo = x.RollNo;
                        nearbyUsers.JoiningYear = x.JoiningYear;
                        nearbyUsers.LeavingYear = x.LeavingYear;
                        nearbyUsers.Distance = distance;
                        nearbyUsers.PhoneNumber = x.PhoneNumber;
                        nearbyUsers.Profession = x.Profession;
                        nearbyUsers.ProfileLink = x.ProfileLink;
                        if (x.LocationTime != null)
                        {
                            nearbyUsers.LocationDaysOld = Math.Abs(Math.Round((DateTime.Now - x.LocationTime).Value.TotalDays)).ToString() + " days ago";
                        }
                        else
                        {
                            nearbyUsers.LocationDaysOld = "No data available";
                        }
                        nearbyUserList.Add(nearbyUsers);
                    }
                }
                if (!loc.CitySearch)//Store city location data as user location data
                {
                    AspNetUser.Latitude = (decimal)loc.Latitude;
                    AspNetUser.Longitude = (decimal)loc.Longitude;
                    AspNetUser.LocationTime = DateTime.Now;
                    db.Entry(AspNetUser).State = EntityState.Modified;
                    db.SaveChanges();
                }
                //nearbyUsers = nearbyUserList.Where(r => r.Distance == nearbyUserList.Min(d => d.Distance)).FirstOrDefault();
                if (nearbyUserList.Count > 0)
                {
                    //nearbyUsers = nearbyUserList.Where(r => r.Distance == nearbyUserList.Min(d => d.Distance)).FirstOrDefault();
                    return Ok(nearbyUserList.OrderBy(u => u.Distance));
                }
                else
                {
                    return Ok("No records found");
                }
            }
            catch (Exception ex)
            {
                return Ok(ex.Message);
            }
        }

        [HttpPut]
        [ActionName("UpdatePartyUserLocation")]
        public IHttpActionResult UpdatePartyUserLocation(UserLocation loc)
        {
            LogApi.Log(User.Identity.GetUserId(), "UpdateUserLocation from " + loc.Filter + " " + User.Identity.GetUserName());

            List<AspNetUser> list_AspNetUser = null;
            if (loc.Radius == 0)
            {
                loc.Radius = 50000;
            }
            else
            {
                loc.Radius = loc.Radius * 1000;
            }
            try
            {
                //Get employee
                var userId = User.Identity.GetUserId();
                AspNetUser AspNetUser = db.AspNetUsers.Where(e => e.Id == userId).FirstOrDefault();

                list_AspNetUser = db.AspNetUsers.Where(u => u.School == AspNetUser.School && (u.tbl_PartyStatus.FirstOrDefault().col_IsAvailable=="Yes" || u.tbl_PartyStatus.FirstOrDefault().col_IsAvailable == "May be") && u.tbl_PartyStatus.FirstOrDefault().col_Date.Day == DateTime.Now.Day).ToList();

                List<NearbyUsers> nearbyUserList = new List<NearbyUsers>();
                NearbyUsers nearbyUsers;
                foreach (var x in list_AspNetUser.Where(u => u.Id != userId))
                {
                    GeoCoordinate eCoord = null;
                    if (loc.CitySearch)
                    {
                        eCoord = new GeoCoordinate(loc.CityLatitude, loc.CityLongitude);
                    }
                    else
                    {
                        eCoord = new GeoCoordinate((double)AspNetUser.Latitude, (double)AspNetUser.Longitude);
                    }
                    var sCoord = new GeoCoordinate(Convert.ToDouble(x.Latitude), Convert.ToDouble(x.Longitude));
                    double distance = sCoord.GetDistanceTo(eCoord);
                    if (distance < loc.Radius && distance > 0 && sCoord.Latitude != 0 && sCoord.Longitude != 0)
                    {
                        nearbyUsers = new NearbyUsers();
                        nearbyUsers.Id = x.Id;
                        nearbyUsers.FirstName = x.FirstName;
                        nearbyUsers.LastName = x.LastName;
                        nearbyUsers.Address = x.Address;
                        nearbyUsers.BloodGroup = x.BloodGroup;
                        nearbyUsers.City = x.City;
                        nearbyUsers.Department = x.Department;
                        nearbyUsers.School = x.School;
                        nearbyUsers.Designation = x.Designation;
                        nearbyUsers.House = x.House;
                        nearbyUsers.PostalCode = x.PostalCode;
                        nearbyUsers.Posting = x.Posting;
                        //nearbyUsers.RollNo = x.RollNo;
                        nearbyUsers.JoiningYear = x.JoiningYear;
                        nearbyUsers.LeavingYear = x.LeavingYear;
                        nearbyUsers.Distance = distance;
                        nearbyUsers.PhoneNumber = x.PhoneNumber;
                        nearbyUsers.Profession = x.Profession;
                        nearbyUsers.ProfileLink = x.ProfileLink;
                        if (x.LocationTime != null)
                        {
                            nearbyUsers.LocationDaysOld = Math.Abs(Math.Round((DateTime.Now - x.LocationTime).Value.TotalDays)).ToString() + " days ago";
                        }
                        else
                        {
                            nearbyUsers.LocationDaysOld = "No data available";
                        }
                       
                        nearbyUserList.Add(nearbyUsers);
                    }
                }
                if (!loc.CitySearch)//Store city location data as user location data
                {
                    AspNetUser.Latitude = (decimal)loc.Latitude;
                    AspNetUser.Longitude = (decimal)loc.Longitude;
                    AspNetUser.LocationTime = DateTime.Now;
                    db.Entry(AspNetUser).State = EntityState.Modified;
                    db.SaveChanges();
                }
                if (nearbyUserList.Count > 0)
                {
                    //nearbyUsers = nearbyUserList.Where(r => r.Distance == nearbyUserList.Min(d => d.Distance)).FirstOrDefault();
                    return Ok(nearbyUserList.OrderBy(u => u.Distance));
                }
                else
                {
                    return Ok("No records found");
                }
            }
            catch (Exception ex)
            {
                return Ok(ex.Message);
            }
        }

        [HttpPut]
        [ActionName("SearchMembers")]
        public IHttpActionResult SearchMembers(UserSearch search)
        {
            List<AspNetUser> list_AspNetUser = null;
            List<SearchedUsers> searchedUsers = new List<SearchedUsers>();
            StringBuilder query = new StringBuilder();
            try
            {
                var userId = User.Identity.GetUserId();
                bool validSearch = false;
                //Get employee
                List<string> searchList = new List<string>();
                if (search.School != "All Departments")
                {
                    validSearch = true;
                    searchList.Add(" School = '" + search.School + "' ");
                }
                if (search.Batch != "All Batches")
                {
                    validSearch = true;
                    searchList.Add(" LeavingYear = '" + search.Batch + "' ");
                }
                if (search.Profession != "All Professions" && search.Profession != "All Categories")
                {
                    validSearch = true;
                    searchList.Add(" Profession = '" + search.Profession + "' ");
                }
                if (!string.IsNullOrEmpty(search.FirstName))
                {
                    validSearch = true;
                    searchList.Add(" FirstName like '%" + search.FirstName + "%' ");
                }
                if (!string.IsNullOrEmpty(search.LastName))
                {
                    validSearch = true;
                    searchList.Add(" LastName like '%" + search.LastName + "%' ");
                }
                if (!string.IsNullOrEmpty(search.Department))
                {
                    validSearch = true;
                    searchList.Add(" Department like '%" + search.Department + "%' ");
                }
                if (!string.IsNullOrEmpty(search.Designation))
                {
                    validSearch = true;
                    searchList.Add(" Designation like '%" + search.Designation + "%' ");
                }
                if (search.City != "All Cities")
                {
                    validSearch = true;
                    searchList.Add(" Posting = '" + search.City + "' ");
                }
                if (!string.IsNullOrEmpty(search.RollNo))
                {
                    validSearch = true;
                    searchList.Add(" RollNo = '" + search.RollNo + "' ");
                }
                if (validSearch)
                {

                    query.Append("select * from AspNetUsers where ");

                    foreach (var s in searchList)
                    {
                        query.Append(s);
                        if (searchList.Count > 1)
                        {
                            query.Append(" and ");
                        }
                    }
                    if (query.ToString().EndsWith(" and "))
                    {
                        query.Remove(query.ToString().Length - 5, 5);
                    }
                    LogApi.Log(User.Identity.GetUserId(), "SearchMembers " + query.ToString());
                    list_AspNetUser = db.AspNetUsers.SqlQuery(query.ToString()).ToList<AspNetUser>();

                    SearchedUsers SearchedUser = null;
                    foreach (var x in list_AspNetUser)
                    {
                        SearchedUser = new SearchedUsers();
                        SearchedUser.Id = x.Id;
                        SearchedUser.FirstName = x.FirstName;
                        SearchedUser.LastName = x.LastName;
                        SearchedUser.Address = x.Address;
                        SearchedUser.BloodGroup = x.BloodGroup;
                        SearchedUser.City = x.City;
                        SearchedUser.Department = x.Department;
                        SearchedUser.School = x.School;
                        SearchedUser.Designation = x.Designation;
                        SearchedUser.House = x.House;
                        SearchedUser.PostalCode = x.PostalCode;
                        SearchedUser.Posting = x.Posting;
                        //SearchedUser.RollNo = x.RollNo;
                        SearchedUser.JoiningYear = x.JoiningYear;
                        SearchedUser.LeavingYear = x.LeavingYear;
                        SearchedUser.PhoneNumber = x.PhoneNumber;
                        SearchedUser.Profession = x.Profession;
                        SearchedUser.ProfileLink = x.ProfileLink;
                        searchedUsers.Add(SearchedUser);
                    }
                }
            }
            catch (Exception ex)
            {
                return Ok(ex.Message + "  " + query);
            }
            if (searchedUsers.Count > 0)
            {
                return Ok(searchedUsers.OrderBy(u => u.JoiningYear));
            }
            else
            {
                return Ok("No records found");
            }
           
        }

        [ResponseType(typeof(FileUpload))]
        public IHttpActionResult PostFileUpload()
        {
            if (HttpContext.Current.Request.Files.AllKeys.Any())
            {
                // Get the uploaded image from the Files collection  
                var httpPostedFile = HttpContext.Current.Request.Files[0];
                if (httpPostedFile != null)
                {
                    try
                    {
                        FileUpload imgupload = new FileUpload();
                        int length = httpPostedFile.ContentLength;
                        imgupload.ImageData = new byte[length]; //get imagedata  
                        httpPostedFile.InputStream.Read(imgupload.ImageData, 0, length);
                        imgupload.ImageName = Path.GetFileName(httpPostedFile.FileName);

                        var x = User.Identity.GetUserId().ToString();
                        var user = db.tbl_Photo.Where(u => u.col_UserId == x).FirstOrDefault();
                        if (user == null)
                        {
                            tbl_Photo tbl_Photo = new tbl_Photo();
                            tbl_Photo.col_Photo = imgupload.ImageData;
                            tbl_Photo.col_UserId = x;
                            db.tbl_Photo.Add(tbl_Photo);
                        }
                        else
                        {
                            user.col_Photo = imgupload.ImageData;
                            db.Entry(user).State = EntityState.Modified;
                        }

                        db.SaveChanges();
                        return Ok("Success");
                    }
                    catch (Exception ex)
                    {
                        return Ok("Exception: " + ex.Message + " " + ex.InnerException);
                    }
                }
            }
            return Ok("Fail");
        }

        public static Bitmap ResizeImage(Image image, int width, int height)
        {
            var destRect = new Rectangle(0, 0, width, height);
            var destImage = new Bitmap(width, height);

            destImage.SetResolution(image.HorizontalResolution, image.VerticalResolution);

            using (var graphics = Graphics.FromImage(destImage))
            {
                graphics.CompositingMode = CompositingMode.SourceCopy;
                graphics.CompositingQuality = CompositingQuality.HighQuality;
                graphics.InterpolationMode = InterpolationMode.HighQualityBicubic;
                graphics.SmoothingMode = SmoothingMode.HighQuality;
                graphics.PixelOffsetMode = PixelOffsetMode.HighQuality;

                using (var wrapMode = new ImageAttributes())
                {
                    wrapMode.SetWrapMode(WrapMode.TileFlipXY);
                    graphics.DrawImage(image, destRect, 0, 0, image.Width, image.Height, GraphicsUnit.Pixel, wrapMode);
                }
            }

            return destImage;
        }

        public Image byteArrayToImage(byte[] byteArrayIn)
        {
            MemoryStream ms = new MemoryStream(byteArrayIn);
            Image returnImage = Image.FromStream(ms);
            return returnImage;
        }

        public byte[] imageToByteArray(System.Drawing.Image imageIn)
        {
            MemoryStream ms = new MemoryStream();
            imageIn.Save(ms, System.Drawing.Imaging.ImageFormat.Gif);
            return ms.ToArray();
        }

        public class UserAlbum
        {
            public int col_UserAlbumID { get; set; }
            public byte[] col_Photo { get; set; }
            public string col_UserId { get; set; }
            public string col_Description { get; set; }
            public System.DateTime col_PostedDateTime { get; set; }
        }

        [HttpGet]
        [ActionName("GetPhotoList")]
        public IEnumerable<UserAlbum> GetThumbnailList()
        {
            List<UserAlbum> userAlbumList = new List<UserAlbum>();
            UserAlbum userAlbum;
            IEnumerable<tbl_UserAlbum> source = null;
            // Return List of Customer  
            //source = db.tbl_UserAlbum.Where(p=>p.col_PostedDateTime.Day==DateTime.Now.Day).ToList();
            source = db.tbl_UserAlbum.ToList();
            foreach (var p in source)
            {
                var postedById = db.AspNetUsers.Where(m => m.Id == p.col_UserId).FirstOrDefault();
                var postedBy = postedById.FirstName + " " + postedById.LastName + " " + postedById.School + " " + postedById.RollNo;
                userAlbum = new UserAlbum();
                userAlbum.col_Description = p.col_Description;
                userAlbum.col_Photo = p.col_Photo;
                userAlbum.col_UserId = postedBy;
                userAlbum.col_UserAlbumID = p.col_UserAlbumID;
                userAlbumList.Add(userAlbum);

            }
            return userAlbumList;
        }

        [HttpGet]
        [ActionName("GetAlbumThumbnail")]
        public HttpResponseMessage GetAlbumThumbnail(string id)
        {
            List<UserAlbum> userAlbumList = new List<UserAlbum>();

            int photoId = Convert.ToInt32(id);
            var data = db.tbl_UserAlbum.Where(u => u.col_UserAlbumID == photoId).FirstOrDefault().col_Photo;
            byte[] imgData = data;
            MemoryStream ms = new MemoryStream(imgData);
            HttpResponseMessage response = new HttpResponseMessage();
            response.Content = new ByteArrayContent(ms.ToArray());
            ms.Close();
            ms.Dispose();
            response.Content.Headers.ContentType = new MediaTypeHeaderValue("image/jpg");
            response.StatusCode = HttpStatusCode.OK;
            return response;
        }

        [HttpGet]
        [ActionName("GetAlbumPhoto")]
        public HttpResponseMessage GetAlbumPhoto(string id)
        {
            int photoId = Convert.ToInt32(id);
            var data = db.tbl_UserAlbum.Where(u => u.col_UserAlbumID == photoId).FirstOrDefault().col_BigPhoto;
            byte[] imgData = data;
            MemoryStream ms = new MemoryStream(imgData);
            HttpResponseMessage response = new HttpResponseMessage();
            response.Content = new ByteArrayContent(ms.ToArray());
            ms.Close();
            ms.Dispose();
            response.Content.Headers.ContentType = new MediaTypeHeaderValue("image/jpg");
            response.StatusCode = HttpStatusCode.OK;
            return response;
        }

        [ResponseType(typeof(FileUpload))]
        public IHttpActionResult PostAlbumPhotoUpload(string description)
        {
            if (HttpContext.Current.Request.Files.AllKeys.Any())
            {
                // Get the uploaded image from the Files collection  
                var httpPostedFile = HttpContext.Current.Request.Files[0];
                if (httpPostedFile != null)
                {
                    try
                    {
                        FileUpload imgupload = new FileUpload();
                        int length = httpPostedFile.ContentLength;
                        imgupload.ImageData = new byte[length]; //get imagedata  
                        httpPostedFile.InputStream.Read(imgupload.ImageData, 0, length);
                        imgupload.ImageName = Path.GetFileName(httpPostedFile.FileName);

                        var x = User.Identity.GetUserId().ToString();
                        var user = db.tbl_UserAlbum.Where(u => u.col_UserId == x).FirstOrDefault();

                        tbl_UserAlbum userAlbum = new tbl_UserAlbum();
                        Image img = byteArrayToImage(imgupload.ImageData);

                        userAlbum.col_Photo = imageToByteArray(ResizeImage(img, 400, 200));
                        userAlbum.col_BigPhoto = imgupload.ImageData;
                        userAlbum.col_Description = description;
                        userAlbum.col_PostedDateTime = DateTime.Now;
                        userAlbum.col_UserId = x;
                        db.tbl_UserAlbum.Add(userAlbum);

                        db.SaveChanges();
                        return Ok("Success");
                    }
                    catch (Exception ex)
                    {
                        return Ok("Exception: " + ex.Message + " " + ex.InnerException);
                    }
                }
            }
            return Ok("Fail");
        }

        [HttpGet]
        [ActionName("UserImage")]
        public HttpResponseMessage Get_UserImage(string id)
        {
            var data = db.tbl_Photo.Where(u => u.col_UserId == id).FirstOrDefault().col_Photo;
            byte[] imgData = data;
            MemoryStream ms = new MemoryStream(imgData);
            HttpResponseMessage response = new HttpResponseMessage();
            response.Content = new ByteArrayContent(ms.ToArray());
            ms.Close();
            ms.Dispose();
            response.Content.Headers.ContentType = new MediaTypeHeaderValue("image/jpg");
            response.StatusCode = HttpStatusCode.OK;
            return response;
        }

        //[HttpGet]
        //[ActionName("ProfileImage")]
        //public HttpResponseMessage Get_ProfileImage()
        //{
        //    var userId = User.Identity.GetUserId().ToString();
        //    var data = db.tbl_Photo.Where(u => u.col_UserId == userId).FirstOrDefault().col_Photo;
        //    byte[] imgData = data;
        //    MemoryStream ms = new MemoryStream(imgData);
        //    HttpResponseMessage response = new HttpResponseMessage();
        //    response.Content = new ByteArrayContent(ms.ToArray());
        //    ms.Close();
        //    ms.Dispose();
        //    response.Content.Headers.ContentType = new MediaTypeHeaderValue("image/jpg");
        //    response.StatusCode = HttpStatusCode.OK;
        //    return response;
        //}

        // GET: api/AspNetUsers/5
        [HttpGet]
        [ActionName("GetUser")]
        public async Task<IHttpActionResult> GetAspNetUser(string id)
        {
            LogApi.Log(User.Identity.GetUserId(), "GetUser from " + User.Identity.GetUserName());

            AspNetUser aspNetUser = await db.AspNetUsers.FindAsync(id);
            if (aspNetUser == null)
            {
                return NotFound();
            }

            return Ok(aspNetUser);
        }

        [HttpGet]
        [ActionName("Alerts")]
        public async Task<IHttpActionResult> GetAlert()
        {
            LogApi.Log(User.Identity.GetUserId(), "Alerts from " + User.Identity.GetUserName());

            String alert = db.tbl_Alerts.FirstOrDefault().col_Alert.ToString();
            return Ok(alert);
        }

        [HttpGet]
        [ActionName("GetVersion")]
        public async Task<IHttpActionResult> GetVersion()
        {
            var userId = User.Identity.GetUserId();
            String version = db.tbl_Version.FirstOrDefault().col_Version;
            return Ok(version);
        }
        public class EditUserModel
        {
            public string Id { get; set; }
            public string FirstName { get; set; }
            public string LastName { get; set; }
            public string Email { get; set; }
            public bool EmailConfirmed { get; set; }
            public string PasswordHash { get; set; }
            public string SecurityStamp { get; set; }
            public string PhoneNumber { get; set; }
            public bool PhoneNumberConfirmed { get; set; }
            public bool TwoFactorEnabled { get; set; }
            public Nullable<System.DateTime> LockoutEndDateUtc { get; set; }
            public bool LockoutEnabled { get; set; }
            public int AccessFailedCount { get; set; }
            public string UserName { get; set; }
            public string Address { get; set; }
            public System.DateTime DateOfBirth { get; set; }
            public string City { get; set; }
            public string State { get; set; }
            public string PostalCode { get; set; }
            public int RollNo { get; set; }
            public System.DateTime JoiningYear { get; set; }
            public System.DateTime LeavingYear { get; set; }
            public string Designation { get; set; }
            public string Posting { get; set; }
            public string BloodGroup { get; set; }
            public Nullable<decimal> Latitude { get; set; }
            public Nullable<decimal> Longitude { get; set; }
            public string House { get; set; }
            public string Department { get; set; }
            public Nullable<System.DateTime> LocationTime { get; set; }
            public string School { get; set; }
            public bool PhoneVisible { get; set; }
            public bool ShowLocation { get; set; }
            public string Profession { get; set; }
            public string ProfileLink { get; set; }
        }
        // PUT: api/AspNetUsers/5
        //[AllowAnonymous]
        [HttpPut]
        [ActionName("UpdateUser")]
        public async Task<IHttpActionResult> PutAspNetUser(EditUserModel editUserModel)
        {
            LogApi.Log(User.Identity.GetUserId(), "UpdateUser from " + User.Identity.GetUserName() + "  " + editUserModel.FirstName + "  " +
              "LastName " + editUserModel.LastName + "  " +
             "House  " + editUserModel.House + "  " +
             "Address  " + editUserModel.Address + "  " +
             "BloodGroup  " + editUserModel.BloodGroup + "  " +
             "City  " + editUserModel.City + "  " +
             "DateOfBirth " + editUserModel.DateOfBirth + "  " +
            "Department  " + editUserModel.Department + "  " +
             "Designation  " + editUserModel.Designation + "  " +
             "JoiningYear  " + editUserModel.JoiningYear + "  " +
             "LeavingYear  " + editUserModel.LeavingYear + "  " +
             "PhoneNumber  " + editUserModel.PhoneNumber + "  " +
             "PostalCode  " + editUserModel.PostalCode + "  " +
             "Posting  " + editUserModel.Posting + "  " +
            "RollNo  " + editUserModel.RollNo + "  " +
            "School  " + editUserModel.School + "  " +
            "State  " + editUserModel.State + "  " +
            "PhoneVisible  " + editUserModel.PhoneVisible + "  " +
            "ShowLocation  " + editUserModel.ShowLocation + "  " +
            "Profession  " + editUserModel.Profession + "  " +
            "ProfileLink  " + editUserModel.ProfileLink + "  ");

            AspNetUser aspNetUser = db.AspNetUsers.Where(e => e.Id == editUserModel.Id).FirstOrDefault();
            aspNetUser.PasswordHash = aspNetUser.PasswordHash;
            aspNetUser.SecurityStamp = aspNetUser.SecurityStamp;
            aspNetUser.LockoutEnabled = aspNetUser.LockoutEnabled;
            aspNetUser.LockoutEndDateUtc = aspNetUser.LockoutEndDateUtc;
            aspNetUser.TwoFactorEnabled = aspNetUser.TwoFactorEnabled;
            aspNetUser.AccessFailedCount = aspNetUser.AccessFailedCount;
            aspNetUser.FirstName = editUserModel.FirstName;
            aspNetUser.LastName = editUserModel.LastName;
            aspNetUser.House = "House";
            aspNetUser.Address = editUserModel.Address;
            aspNetUser.BloodGroup = editUserModel.BloodGroup;
            aspNetUser.City = editUserModel.City;
            aspNetUser.DateOfBirth = editUserModel.DateOfBirth;
            aspNetUser.Department = editUserModel.Department;
            aspNetUser.Designation = editUserModel.Designation;
            aspNetUser.JoiningYear = editUserModel.JoiningYear;
            aspNetUser.LeavingYear = editUserModel.LeavingYear;
            aspNetUser.PhoneNumber = editUserModel.PhoneNumber;
            aspNetUser.PostalCode = editUserModel.PostalCode;
            aspNetUser.Posting = editUserModel.Posting;
            aspNetUser.RollNo = editUserModel.RollNo;
            aspNetUser.School = editUserModel.School;
            aspNetUser.State = editUserModel.State;
            aspNetUser.PhoneVisible = editUserModel.PhoneVisible;
            aspNetUser.ShowLocation = editUserModel.ShowLocation;
            aspNetUser.Profession = editUserModel.Profession;
            aspNetUser.ProfileLink = editUserModel.ProfileLink;
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            //if (id != aspNetUser.Id)
            //{
            //    return BadRequest();
            //}

            db.Entry(aspNetUser).State = EntityState.Modified;

            try
            {
                await db.SaveChangesAsync();

                try
                {
                    var token = db.tbl_DeviceIds.Where(d => d.col_UserType == "Admin").FirstOrDefault().col_DeviceToken;
                    Notifications.NotifyAsync(token, "Update", editUserModel.School + "#" + editUserModel.LeavingYear + "#" + User.Identity.GetUserId());
                }
                catch (Exception ex)
                {

                }
                return Ok("Success");
            }
            catch (Exception ex)
            {

                //if (!AspNetUserExists(id))
                //{
                //    return NotFound();
                //}
                //else
                //{
                //    throw;
                //}
                return Ok(ex.Message);
            }
        }

        public class ActivateUserModel
        {
            public string UserName { get; set; }
        }

        [HttpPut]
        [ActionName("ActivateUser")]
        public async Task<IHttpActionResult> ActivateUser(ActivateUserModel activateUserModel)
        {
            try
            {
                var adminId = db.tbl_DeviceIds.Where(d => d.col_UserType == "Admin").FirstOrDefault().col_UserID;
                if (User.Identity.GetUserId() == adminId)
                {
                    AspNetUser aspNetUser = db.AspNetUsers.Where(e => e.UserName == activateUserModel.UserName).FirstOrDefault();

                    aspNetUser.PhoneNumberConfirmed = true;

                    //if (!ModelState.IsValid)
                    //{
                    //    return BadRequest(ModelState);
                    //}

                    //if (id != aspNetUser.Id)
                    //{
                    //    return BadRequest();
                    //}

                    db.Entry(aspNetUser).State = EntityState.Modified;


                    await db.SaveChangesAsync();

                    try
                    {
                        var token = db.tbl_DeviceIds.Where(d => d.col_UserID == aspNetUser.Id).FirstOrDefault().col_DeviceToken;
                        Notifications.NotifyAsync(token, "Congratulations", "You are premimium member now");
                    }
                    catch (Exception ex)
                    {

                    }
                    return Ok("Success");
                }
                return Ok("Failed");
            }
            catch (Exception ex)
            {
                return Ok(ex.Message);
            }
        }
        // POST: api/AspNetUsers
        //[ResponseType(typeof(AspNetUser))]
        //public async Task<IHttpActionResult> PostAspNetUser(AspNetUser aspNetUser)
        //{

        //    if (!ModelState.IsValid)
        //    {
        //        return BadRequest(ModelState);
        //    }

        //    db.AspNetUsers.Add(aspNetUser);

        //    try
        //    {
        //        await db.SaveChangesAsync();
        //    }
        //    catch (DbUpdateException)
        //    {
        //        if (AspNetUserExists(aspNetUser.Id))
        //        {
        //            return Conflict();
        //        }
        //        else
        //        {
        //            throw;
        //        }
        //    }

        //    return CreatedAtRoute("DefaultApi", new { id = aspNetUser.Id }, aspNetUser);
        //}

        // DELETE: api/AspNetUsers/5
        [HttpDelete]
        [ActionName("DeleteUser")]
        public async Task<IHttpActionResult> DeleteAspNetUser(string id)
        {
            var adminId = db.tbl_DeviceIds.Where(d => d.col_UserType == "Admin").FirstOrDefault().col_UserID;
            if (User.Identity.GetUserId() == adminId)
            {
                AspNetUser aspNetUser = await db.AspNetUsers.FindAsync(id);
                if (aspNetUser == null)
                {
                    return NotFound();
                }

                db.AspNetUsers.Remove(aspNetUser);
                await db.SaveChangesAsync();

                return Ok("Deleted");
            }
            return Ok("Failed");
        }
        [HttpDelete]
        [ActionName("DeleteUserByEmail")]
        public async Task<IHttpActionResult> DeleteAspNetUserByEmail(string email)
        {
            string id = db.AspNetUsers.Where(u => u.Email == email).FirstOrDefault().Id;
            if (!string.IsNullOrEmpty(id))
            {
                AspNetUser aspNetUser = await db.AspNetUsers.FindAsync(id);
                if (aspNetUser == null)
                {
                    return NotFound();
                }

                db.AspNetUsers.Remove(aspNetUser);
                await db.SaveChangesAsync();

                return Ok("Deleted");
            }
            return Ok("Failed");
        }


        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool AspNetUserExists(string id)
        {
            return db.AspNetUsers.Count(e => e.Id == id) > 0;
        }
    }
}