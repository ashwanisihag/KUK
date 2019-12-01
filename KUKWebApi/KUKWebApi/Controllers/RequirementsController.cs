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
    public class RequirementsController : ApiController
    {
        private KUKEntities db = new KUKEntities();

        public class RequirementCategory
        {
            public string Category { get; set; }
        }

        public partial class RequirementsModel
        {
            public int col_RequirementID { get; set; }
            public string col_RequirementCategory { get; set; }
            public string col_RequirementDescription { get; set; }
            public string col_RequirementContact { get; set; }
            public string col_RequirementEmail { get; set; }
            public string col_PostedById { get; set; }
            public string col_PostedBy { get; set; }
            public System.DateTime col_RequirementPostedDate { get; set; }
        }

        [HttpPut]
        [ActionName("GetRequirement")]
        public IHttpActionResult Gettbl_Requirements(RequirementCategory category)
        {
            LogApi.Log(User.Identity.GetUserId(), "GetRequirement " + User.Identity.GetUserName() );

            try
            {
                List<RequirementsModel> listRequirements = new List<RequirementsModel>();
                RequirementsModel requirementModel;
                //var jobs = db.tbl_Jobs.Where(c => c.col_Category == category.Category);
                var requirments = db.tbl_Requirements;
                foreach (var r in requirments)
                {
                    var postedBy = db.AspNetUsers.Where(m => m.Id == r.col_PostedBy).FirstOrDefault();
                    var postedById = db.AspNetUsers.Where(m => m.Id == r.col_PostedBy).FirstOrDefault();
                    requirementModel = new RequirementsModel();
                    requirementModel.col_RequirementCategory = r.col_RequirementCategory;
                    requirementModel.col_RequirementEmail = r.col_RequirementEmail;
                    requirementModel.col_RequirementContact = r.col_RequirementContact;
                    requirementModel.col_RequirementDescription = r.col_RequirementDescription;
                    requirementModel.col_RequirementPostedDate = r.col_RequirementPostedDate;
                    requirementModel.col_PostedById = postedById.Id;
                    requirementModel.col_PostedBy = postedBy.FirstName + " " + postedBy.LastName + " " + postedBy.RollNo;
                    listRequirements.Add(requirementModel);
                }
                if (listRequirements.Count > 0)
                {
                    listRequirements = listRequirements.OrderByDescending(j => j.col_RequirementPostedDate).ToList();
                    return Ok(listRequirements);

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
        [ActionName("GetMyRequirement")]
        public IQueryable<tbl_Requirements> Gettbl_MyRequirements()
        {
            var id = User.Identity.GetUserId();
            return db.tbl_Requirements.Where(r=>r.col_PostedBy== id);
        }

        // GET: api/Requirements/5
        [ResponseType(typeof(tbl_Requirements))]
        public async Task<IHttpActionResult> Gettbl_Requirements(int id)
        {
            tbl_Requirements tbl_Requirements = await db.tbl_Requirements.FindAsync(id);
            if (tbl_Requirements == null)
            {
                return NotFound();
            }

            return Ok(tbl_Requirements);
        }

        // PUT: api/Requirements/5
        [ResponseType(typeof(void))]
        public async Task<IHttpActionResult> Puttbl_Requirements(int id, tbl_Requirements tbl_Requirements)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != tbl_Requirements.col_RequirementID)
            {
                return BadRequest();
            }

            db.Entry(tbl_Requirements).State = EntityState.Modified;

            try
            {
                await db.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!tbl_RequirementsExists(id))
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

        // POST: api/Requirements
        [HttpPost]
        [ActionName("PostRequirement")]
        public async Task<IHttpActionResult> Posttbl_Requirements(tbl_Requirements tbl_Requirements)
        {
            LogApi.Log(User.Identity.GetUserId(), "PostRequirement " + User.Identity.GetUserName() );

            var id = User.Identity.GetUserId();
            tbl_Requirements.col_PostedBy = id;
            tbl_Requirements.col_RequirementPostedDate = DateTime.Now;
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.tbl_Requirements.Add(tbl_Requirements);
            await db.SaveChangesAsync();
            try
            {
                var tokens = db.tbl_DeviceIds;
                foreach (var d in tokens)
                {
                    Notifications.NotifyAsync(d.col_DeviceToken, "Requirement", "New Requirement: " + tbl_Requirements.col_RequirementCategory + "#" + tbl_Requirements.col_RequirementDescription + "#" +id);
                }
            }
            catch (Exception ex)
            {
                return Ok("Failed");
            }
            return Ok("Posted");
        }

        [HttpDelete]
        [ActionName("DeleteRequirement")]
        public async Task<IHttpActionResult> Deletetbl_Requirements(int id)
        {
            LogApi.Log(User.Identity.GetUserId(), "DeleteRequirement " + User.Identity.GetUserName() );

            tbl_Requirements tbl_Requirements = await db.tbl_Requirements.FindAsync(id);
            if (tbl_Requirements == null)
            {
                return NotFound();
            }

            db.tbl_Requirements.Remove(tbl_Requirements);
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

        private bool tbl_RequirementsExists(int id)
        {
            return db.tbl_Requirements.Count(e => e.col_RequirementID == id) > 0;
        }
    }
}