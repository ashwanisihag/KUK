using Microsoft.AspNet.Identity;
using Microsoft.AspNet.Identity.EntityFramework;
using System;
using System.Drawing;
using System.Security.Claims;
using System.Threading.Tasks;

namespace KUKWebApi.Models
{
    // You can add profile data for the user by adding more properties to your ApplicationUser class, please visit http://go.microsoft.com/fwlink/?LinkID=317594 to learn more.
    public class ApplicationUser : IdentityUser
    {
        public async Task<ClaimsIdentity> GenerateUserIdentityAsync(UserManager<ApplicationUser> manager, string authenticationType)
        {
            // Note the authenticationType must match the one defined in CookieAuthenticationOptions.AuthenticationType
            var userIdentity = await manager.CreateIdentityAsync(this, authenticationType);
            // Add custom user claims here
            return userIdentity;
        }
        public string School { get; set; }
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public DateTime DateOfBirth { get; set; }
        public string Address { get; set; }
        public string City { get; set; }
        public string State { get; set; }
        public string PostalCode { get; set; }
        public int RollNo { get; set; }
        public DateTime JoiningYear { get; set; }
        public DateTime LeavingYear { get; set; }
        public string Designation { get; set; }
        public string Department { get; set; }
        public string Posting { get; set; }
        public string BloodGroup { get; set; }
        //public Image Photo { get; set; }
        public decimal Latitude { get; set; }
        public decimal Longitude { get; set; }
        public string House { get; set; }
        public bool ShowLocation { get; set; }
        public string Profession { get; set; }
        public bool PhoneVisible { get; set; }
        public string ProfileLink { get; set; }
    }

    public class ApplicationDbContext : IdentityDbContext<ApplicationUser>
    {
        public ApplicationDbContext()
            : base("DefaultConnection", throwIfV1Schema: false)
        {
        }

        public static ApplicationDbContext Create()
        {
            return new ApplicationDbContext();
        }
    }
}