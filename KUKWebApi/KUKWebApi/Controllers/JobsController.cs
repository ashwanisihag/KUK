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
using KUKWebApi;
using Microsoft.AspNet.Identity;
using Newtonsoft.Json;
using System.Text;
using System.Web.Http.Cors;

namespace KUKWebApi.Controllers
{
    [Authorize]
    [EnableCors(origins: "*", headers: "*", methods: "*")]
    public class JobsController : ApiController
    {
        private KUKEntities db = new KUKEntities();

        public class JobCategory
        {
            public string Category { get; set; }
        }

        public partial class JobsModel
        {
            public int col_JobID { get; set; }
            public string col_JobTitle { get; set; }
            public string col_JobDescription { get; set; }
            public System.DateTime col_PostDateTime { get; set; }
            public bool col_Expired { get; set; }
            public string col_Category { get; set; }
            public string col_ContactNumber { get; set; }
            public string col_ContactEmail { get; set; }
            public string col_PostedBy { get; set; }
            public string col_PostedById { get; set; }
        }

        // GET: api/Jobs
        [HttpPut]
        [ActionName("GetJobs")]
        public IHttpActionResult Gettbl_Jobs(JobCategory category)
        {
            LogApi.Log(User.Identity.GetUserId(), "GetJobs " + User.Identity.GetUserName() );

            try
            {
                List<JobsModel> listJobs = new List<JobsModel>();
                JobsModel JobsModel;
                //var jobs = db.tbl_Jobs.Where(c => c.col_Category == category.Category);
                var jobs = db.tbl_Jobs;
                foreach (var j in jobs)
                {
                    var postedBy = db.AspNetUsers.Where(m => m.Id == j.col_PostedBy).FirstOrDefault();
                    var postedById = db.AspNetUsers.Where(m => m.Id == j.col_PostedBy).FirstOrDefault();
                    JobsModel = new JobsModel();
                    JobsModel.col_Category = j.col_Category;
                    JobsModel.col_ContactEmail = j.col_ContactEmail;
                    JobsModel.col_ContactNumber = j.col_ContactNumber;
                    JobsModel.col_Expired = j.col_Expired;
                    JobsModel.col_JobDescription = j.col_JobDescription;
                    JobsModel.col_JobTitle = j.col_JobTitle;
                    JobsModel.col_PostDateTime = j.col_PostDateTime;
                    JobsModel.col_PostedById = postedById.Id;
                    JobsModel.col_PostedBy = postedBy.FirstName + " " + postedBy.LastName + " " + postedBy.RollNo;
                    listJobs.Add(JobsModel);
                }
                if (listJobs.Count > 0)
                {
                    listJobs= listJobs.OrderByDescending(j=>j.col_PostDateTime).ToList();
                    return Ok(listJobs);
                    
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
        [ActionName("GetMyJobs")]
        public IHttpActionResult GettblMy_Jobs()
        {
            LogApi.Log(User.Identity.GetUserId(), "GetMyJobs " + User.Identity.GetUserName() );

            try
            {
                List<JobsModel> listJobs = new List<JobsModel>();
                JobsModel JobsModel;
                var id = User.Identity.GetUserId();
                var jobs = db.tbl_Jobs.Where(j=>j.col_PostedBy==id);
                foreach (var j in jobs)
                {
                    var postedBy = db.AspNetUsers.Where(m => m.Id == j.col_PostedBy).FirstOrDefault();
                    var postedById = db.AspNetUsers.Where(m => m.Id == j.col_PostedBy).FirstOrDefault();
                    JobsModel = new JobsModel();
                    JobsModel.col_Category = j.col_Category;
                    JobsModel.col_ContactEmail = j.col_ContactEmail;
                    JobsModel.col_ContactNumber = j.col_ContactNumber;
                    JobsModel.col_Expired = j.col_Expired;
                    JobsModel.col_JobDescription = j.col_JobDescription;
                    JobsModel.col_JobID = j.col_JobID;
                    JobsModel.col_JobTitle = j.col_JobTitle;
                    JobsModel.col_PostDateTime = j.col_PostDateTime;
                    JobsModel.col_PostedById = postedById.Id;
                    JobsModel.col_PostedBy = postedBy.FirstName + " " + postedBy.LastName + " " + postedBy.RollNo;
                    listJobs.Add(JobsModel);
                }
                if (listJobs.Count > 0)
                {
                    return Ok(listJobs);

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

        // GET: api/Jobs/5
        [ResponseType(typeof(tbl_Jobs))]
        public async Task<IHttpActionResult> Gettbl_Jobs(int id)
        {
            tbl_Jobs tbl_Jobs = await db.tbl_Jobs.FindAsync(id);
            if (tbl_Jobs == null)
            {
                return NotFound();
            }

            return Ok(tbl_Jobs);
        }

        // PUT: api/Jobs/5
        [ResponseType(typeof(void))]
        public async Task<IHttpActionResult> Puttbl_Jobs(int id, tbl_Jobs tbl_Jobs)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != tbl_Jobs.col_JobID)
            {
                return BadRequest();
            }

            db.Entry(tbl_Jobs).State = EntityState.Modified;

            try
            {
                await db.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!tbl_JobsExists(id))
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

        // POST: api/Jobs
        [HttpPost]
        [ActionName("PostJob")]
        public async Task<IHttpActionResult> Posttbl_Jobs(tbl_Jobs tbl_Jobs)
        {
            LogApi.Log(User.Identity.GetUserId(), "PostJob " + User.Identity.GetUserName() );

            var id = User.Identity.GetUserId();
            tbl_Jobs.col_PostedBy = id;
            tbl_Jobs.col_Expired = false;
            tbl_Jobs.col_PostDateTime = DateTime.Now;
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.tbl_Jobs.Add(tbl_Jobs);
            await db.SaveChangesAsync();
            try
            {
                var tokens = db.tbl_DeviceIds;
                foreach (var d in tokens)
                {
                    Notifications.NotifyAsync(d.col_DeviceToken, "Job", "New job: " +tbl_Jobs.col_JobTitle + "#" + tbl_Jobs.col_JobDescription);
                }
            }
            catch (Exception ex)
            {

            }
            return Ok("Posted");
        }

        // DELETE: api/Jobs/5
        [HttpDelete]
        [ActionName("DeleteJob")]
        public async Task<IHttpActionResult> Deletetbl_Jobs(int id)
        {
            LogApi.Log(User.Identity.GetUserId(), "DeleteJob " + User.Identity.GetUserName() );

            tbl_Jobs tbl_Jobs = await db.tbl_Jobs.FindAsync(id);
            if (tbl_Jobs == null)
            {
                return NotFound();
            }

            db.tbl_Jobs.Remove(tbl_Jobs);
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

        private bool tbl_JobsExists(int id)
        {
            return db.tbl_Jobs.Count(e => e.col_JobID == id) > 0;
        }
    }
}