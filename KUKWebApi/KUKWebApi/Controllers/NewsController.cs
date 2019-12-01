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
    public class NewsController : ApiController
    {
        private KUKEntities db = new KUKEntities();

        // GET: api/News
        [HttpGet]
        [ActionName("GetNews")]
        public IQueryable<tbl_News> Gettbl_News()
        {
            LogApi.Log(User.Identity.GetUserId(), "GetNews " + User.Identity.GetUserName() );

            var dt = DateTime.Now.Date;
            return db.tbl_News.Where(n=>n.col_NewsDateTime.Day== dt.Day && n.col_NewsDateTime.Month == dt.Month && n.col_NewsDateTime.Year == dt.Year);
        }

        // GET: api/News/5
        [ResponseType(typeof(tbl_News))]
        public async Task<IHttpActionResult> Gettbl_News(int id)
        {
            tbl_News tbl_News = await db.tbl_News.FindAsync(id);
            if (tbl_News == null)
            {
                return NotFound();
            }

            return Ok(tbl_News);
        }

        // PUT: api/News/5
        [ResponseType(typeof(void))]
        public async Task<IHttpActionResult> Puttbl_News(int id, tbl_News tbl_News)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != tbl_News.col_NewsID)
            {
                return BadRequest();
            }

            db.Entry(tbl_News).State = EntityState.Modified;

            try
            {
                await db.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!tbl_NewsExists(id))
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

        // POST: api/News
        [HttpPost]
        [ActionName("PostNews")]
        public async Task<IHttpActionResult> Posttbl_News(tbl_News tbl_News)
        {
            LogApi.Log(User.Identity.GetUserId(), "PostNews " + User.Identity.GetUserName() );

            tbl_News.col_NewsDateTime = DateTime.Now;
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.tbl_News.Add(tbl_News);
            await db.SaveChangesAsync();
            try
            {    
                var tokens = db.tbl_DeviceIds;
                foreach (var d in tokens)
                {
                    Notifications.NotifyAsync(d.col_DeviceToken, "News","News: "+  tbl_News.col_News);
                }
            }
            catch(Exception ex)
            {

            }
            return Ok("Posted");
        }

        // DELETE: api/News/5
        [HttpDelete]
        [ActionName("DeleteNews")]
        public async Task<IHttpActionResult> Deletetbl_News(int id)
        {
            LogApi.Log(User.Identity.GetUserId(), "DeleteNews " + User.Identity.GetUserName() );

            tbl_News tbl_News = await db.tbl_News.FindAsync(id);
            if (tbl_News == null)
            {
                return NotFound();
            }

            db.tbl_News.Remove(tbl_News);
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

        private bool tbl_NewsExists(int id)
        {
            return db.tbl_News.Count(e => e.col_NewsID == id) > 0;
        }
    }
}