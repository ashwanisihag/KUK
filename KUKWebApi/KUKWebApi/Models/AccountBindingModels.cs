using System;
using System.ComponentModel.DataAnnotations;
using System.Drawing;
using Newtonsoft.Json;

namespace KUKWebApi.Models
{
    // Models used as parameters to AccountController actions.

    public class AddExternalLoginBindingModel
    {
        [Required]
        [Display(Name = "External access token")]
        public string ExternalAccessToken { get; set; }
    }

    public class ChangePasswordBindingModel
    {
        [Required]
        [DataType(DataType.Password)]
        [Display(Name = "Current password")]
        public string OldPassword { get; set; }

        [Required]
        [StringLength(100, ErrorMessage = "The {0} must be at least {2} characters long.", MinimumLength = 6)]
        [DataType(DataType.Password)]
        [Display(Name = "New password")]
        public string NewPassword { get; set; }

        [DataType(DataType.Password)]
        [Display(Name = "Confirm new password")]
        [Compare("NewPassword", ErrorMessage = "The new password and confirmation password do not match.")]
        public string ConfirmPassword { get; set; }
    }

    public class UploadImage
    {
        public byte[] Image { get; set; }
    }

    public class RegisterBindingModel
    {
        [Required]
        [Display(Name = "School Name")]
        public string School { get; set; }

        [Required]
        [Display(Name = "User Name")]
        public string UserName { get; set; }

        [Required]
        [Display(Name = "First Name")]
        public string FirstName { get; set; }
        [Required]
        [Display(Name = "Last Name")]
        public string LastName { get; set; }

        [Required]
        [Display(Name = "DOB")]
        public DateTime DateOfBirth { get; set; }

        [Required]
        [Display(Name = "Phone")]
        public string PhoneNumber { get; set; }

        [Required]
        public string Email { get; set; }

        public string Address { get; set; }

        public string City { get; set; }

        public string State { get; set; }

        // Use a sensible display name for views:
        [Display(Name = "Postal Code")]
        public string PostalCode { get; set; }

        [Required]
        [Display(Name = "Roll No")]
        public int RollNo { get; set; }

        [Required]
        [Display(Name = "Joining Year")]
        public DateTime JoiningYear { get; set; }

        [Required]
        [Display(Name = "Leaving Year")]
        public DateTime LeavingYear { get; set; }

        [Display(Name = "Designation")]
        public string Designation { get; set; }

        [Display(Name = "DepartMent")]
        public string DepartMent { get; set; }

        public string Posting { get; set; }

        [Display(Name = "Blood Group")]
        public string BloodGroup { get; set; }

        //public byte[] Image { get; set; }

        public decimal Latitude { get; set; }

        public decimal Longitude { get; set; }

        //[Required]
        public string House { get; set; }

        [Required]
        [StringLength(100, ErrorMessage = "The {0} must be at least {2} characters long.", MinimumLength = 6)]
        [DataType(DataType.Password)]
        [Display(Name = "Password")]
        public string Password { get; set; }

        [DataType(DataType.Password)]
        [Display(Name = "Confirm password")]
        [Compare("Password", ErrorMessage = "The password and confirmation password do not match.")]
        public string ConfirmPassword { get; set; }

        [Required]
        [Display(Name = "UserRole")]
        public string UserRole { get; set; }
        [Required]
        [Display(Name = "Show Location")]
        public bool ShowLocation { get; set; }
        [Required]
        [Display(Name = "PhoneVisible")]
        public bool PhoneVisible { get; set; }
        [Required]
        [Display(Name = "Profession")]
        public string Profession { get; set; }
        [Display(Name = "ProfileLink")]
        public string ProfileLink { get; set; }
    }

    public class UpdateBindingModel
    {
        [Required]
        [Display(Name = "First Name")]
        public string FirstName { get; set; }
        [Required]
        [Display(Name = "Last Name")]
        public string LastName { get; set; }

        [Required]
        [Display(Name = "DOB")]
        public DateTime DateOfBirth { get; set; }

        [Required]
        [Display(Name = "Phone")]
        public string PhoneNumber { get; set; }

        [Required]
        [Display(Name = "Email")]
        public string Email { get; set; }
        [Display(Name = "Address")]
        public string Address { get; set; }
        [Display(Name = "City")]
        public string City { get; set; }
        [Display(Name = "State")]
        public string State { get; set; }

        // Use a sensible display name for views:
        [Display(Name = "Postal Code")]
        public string PostalCode { get; set; }

        [Required]
        [Display(Name = "Roll No")]
        public int RollNo { get; set; }

        [Required]
        [Display(Name = "Joining Year")]
        public DateTime JoiningYear { get; set; }

        [Required]
        [Display(Name = "Leaving Year")]
        public DateTime LeavingYear { get; set; }

        [Display(Name = "Designation")]
        public string Designation { get; set; }

        [Display(Name = "DepartMent")]
        public string DepartMent { get; set; }
        [Display(Name = "Posting")]
        public string Posting { get; set; }

        [Display(Name = "Blood Group")]
        public string BloodGroup { get; set; }

        //[Required]
        //[Display(Name = "House")]
        public string House { get; set; }
        [Required]
        [Display(Name = "Show Location")]
        public bool ShowLocation { get; set; }
        [Required]
        [Display(Name = "Profession")]
        public string Profession
        {
            get; set;
        }
        [Display(Name = "ProfileLink")]
        public string ProfileLink { get; set; }
    }
    public class RegisterExternalBindingModel
    {
        [Required]
        [Display(Name = "Email")]
        public string Email { get; set; }
    }

    public class RemoveLoginBindingModel
    {
        [Required]
        [Display(Name = "Login provider")]
        public string LoginProvider { get; set; }

        [Required]
        [Display(Name = "Provider key")]
        public string ProviderKey { get; set; }
    }

    public class SetPasswordBindingModel
    {
        [Required]
        [StringLength(100, ErrorMessage = "The {0} must be at least {2} characters long.", MinimumLength = 6)]
        [DataType(DataType.Password)]
        [Display(Name = "New password")]
        public string NewPassword { get; set; }

        [DataType(DataType.Password)]
        [Display(Name = "Confirm new password")]
        [Compare("NewPassword", ErrorMessage = "The new password and confirmation password do not match.")]
        public string ConfirmPassword { get; set; }
    }
}
