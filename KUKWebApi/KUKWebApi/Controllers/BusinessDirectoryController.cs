using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Description;
using Microsoft.AspNet.Identity;
using KUKWebApi;
using System.Web.Http.Cors;

namespace KUKWebApi.Controllers
{
    [Authorize]
    [EnableCors(origins: "*", headers: "*", methods: "*")]
    public class BusinessDirectoryController : ApiController
    {
        private KUKEntities db = new KUKEntities();

        public class BuinessCategory
        {
            public string Category { get; set; }
        }

        public partial class BusinessDirectoryModel
        {
            public int col_BusinessID { get; set; }
            public string col_BusinessName { get; set; }
            public string col_BusinessCategory { get; set; }
            public string col_BusinessAddress { get; set; }
            public string col_BusinessDescription { get; set; }
            public string col_PostedBy { get; set; }
            public string col_BusinessContact { get; set; }
            public string col_BusinessEmail { get; set; }

            public virtual AspNetUser AspNetUser { get; set; }
        }

        [HttpPut]
        [ActionName("GetBusiness")]
        public IHttpActionResult Gettbl_BusinessDirectory(BuinessCategory category)
        {
            LogApi.Log(User.Identity.GetUserId(), "GetBusiness " + User.Identity.GetUserName() );

            try
            {
                List<BusinessDirectoryModel> listBusiness = new List<BusinessDirectoryModel>();
                BusinessDirectoryModel businessDirectoryModel;
                List<tbl_BusinessDirectory> business = null;
                if (category.Category != "All Categories")
                {
                    business = db.tbl_BusinessDirectory.Where(c => c.col_BusinessCategory == category.Category).ToList();
            }
                else
                {
                    business = db.tbl_BusinessDirectory.ToList();
                }
                foreach (var b in business)
                {
                    var postedBy = db.AspNetUsers.Where(m => m.Id == b.col_PostedBy).FirstOrDefault();
                    var postedById = db.AspNetUsers.Where(m => m.Id == b.col_PostedBy).FirstOrDefault();
                    businessDirectoryModel = new BusinessDirectoryModel();
                    businessDirectoryModel.col_BusinessID = b.col_BusinessID;
                    businessDirectoryModel.col_BusinessCategory = b.col_BusinessCategory;
                    businessDirectoryModel.col_BusinessEmail = b.col_BusinessEmail;
                    businessDirectoryModel.col_BusinessContact = b.col_BusinessContact;
                    businessDirectoryModel.col_BusinessDescription = b.col_BusinessDescription;
                    businessDirectoryModel.col_BusinessName = b.col_BusinessName;
                    businessDirectoryModel.col_BusinessAddress = b.col_BusinessAddress;
                    businessDirectoryModel.col_PostedBy = postedBy.FirstName + " " + postedBy.LastName + " " + postedBy.RollNo;
                    listBusiness.Add(businessDirectoryModel);
                }
                if (listBusiness.Count > 0)
                {
                    return Ok(listBusiness);

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

        [HttpGet]
        [ActionName("GetMyBusiness")]
        public IHttpActionResult Gettbl_MyBusinessDirectory()
        {
            LogApi.Log(User.Identity.GetUserId(), "GetMyBusiness " + User.Identity.GetUserName() );

            try
            {
                List<BusinessDirectoryModel> listBusiness = new List<BusinessDirectoryModel>();
                BusinessDirectoryModel businessDirectoryModel;
                var id = User.Identity.GetUserId();
                var business = db.tbl_BusinessDirectory.Where(c => c.col_PostedBy == id);
                foreach (var b in business)
                {
                    var postedBy = db.AspNetUsers.Where(m => m.Id == b.col_PostedBy).FirstOrDefault();
                    var postedById = db.AspNetUsers.Where(m => m.Id == b.col_PostedBy).FirstOrDefault();
                    businessDirectoryModel = new BusinessDirectoryModel();
                    businessDirectoryModel.col_BusinessID = b.col_BusinessID;
                    businessDirectoryModel.col_BusinessCategory = b.col_BusinessCategory;
                    businessDirectoryModel.col_BusinessEmail = b.col_BusinessEmail;
                    businessDirectoryModel.col_BusinessContact = b.col_BusinessContact;
                    businessDirectoryModel.col_BusinessDescription = b.col_BusinessDescription;
                    businessDirectoryModel.col_BusinessName = b.col_BusinessName;
                    businessDirectoryModel.col_BusinessAddress = b.col_BusinessAddress;
                    businessDirectoryModel.col_PostedBy = postedBy.FirstName + " " + postedBy.LastName + " " + postedBy.RollNo;
                    listBusiness.Add(businessDirectoryModel);
                }
                if (listBusiness.Count > 0)
                {
                    return Ok(listBusiness);

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

        // GET: api/BusinessDirectory/5
        [ResponseType(typeof(tbl_BusinessDirectory))]
        public async Task<IHttpActionResult> Gettbl_BusinessDirectory(int id)
        {
            tbl_BusinessDirectory tbl_BusinessDirectory = await db.tbl_BusinessDirectory.FindAsync(id);
            if (tbl_BusinessDirectory == null)
            {
                return NotFound();
            }

            return Ok(tbl_BusinessDirectory);
        }

        // PUT: api/BusinessDirectory/5
        [ResponseType(typeof(void))]
        public async Task<IHttpActionResult> Puttbl_BusinessDirectory(int id, tbl_BusinessDirectory tbl_BusinessDirectory)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != tbl_BusinessDirectory.col_BusinessID)
            {
                return BadRequest();
            }

            db.Entry(tbl_BusinessDirectory).State = EntityState.Modified;

            try
            {
                await db.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!tbl_BusinessDirectoryExists(id))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return StatusCode(HttpStatusCode.NoContent);
        }


        [HttpPost]
        [ActionName("PostBusiness")]
        public async Task<IHttpActionResult> Posttbl_BusinessDirectory(tbl_BusinessDirectory tbl_BusinessDirectory)
        {
            LogApi.Log(User.Identity.GetUserId(), "PutBusiness " + User.Identity.GetUserName() );

            var id = User.Identity.GetUserId();
            tbl_BusinessDirectory.col_PostedBy = id;
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.tbl_BusinessDirectory.Add(tbl_BusinessDirectory);
            await db.SaveChangesAsync();
            try
            {
                var tokens = db.tbl_DeviceIds;
                foreach (var d in tokens)
                {
                    Notifications.NotifyAsync(d.col_DeviceToken, "Business","New business: " + tbl_BusinessDirectory.col_BusinessCategory+ " " + tbl_BusinessDirectory.col_BusinessDescription);
                }
            }
            catch (Exception ex)
            {

            }
            return Ok("Posted");
        }


        [HttpDelete]
        [ActionName("DeleteBusiness")]
        public async Task<IHttpActionResult> Deletetbl_BusinessDirectory(int id)
        {
            LogApi.Log(User.Identity.GetUserId(), "DeleteBusiness " + User.Identity.GetUserName() );

            tbl_BusinessDirectory tbl_BusinessDirectory = await db.tbl_BusinessDirectory.FindAsync(id);
            if (tbl_BusinessDirectory == null)
            {
                return NotFound();
            }

            db.tbl_BusinessDirectory.Remove(tbl_BusinessDirectory);
            await db.SaveChangesAsync();

            return Ok("Deleted");
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool tbl_BusinessDirectoryExists(int id)
        {
            return db.tbl_BusinessDirectory.Count(e => e.col_BusinessID == id) > 0;
        }
    }
}