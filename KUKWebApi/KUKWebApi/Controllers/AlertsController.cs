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
using System.Web.Http.Cors;

namespace KUKWebApi.Controllers
{
    [EnableCors(origins: "*", headers: "*", methods: "*")]
    public class AlertsController : ApiController
    {
        private KUKEntities db = new KUKEntities();

        // GET: api/Alerts
        public IQueryable<tbl_Alerts> Gettbl_Alerts()
        {
            return db.tbl_Alerts;
        }

        // GET: api/Alerts/5
        [ResponseType(typeof(tbl_Alerts))]
        public async Task<IHttpActionResult> Gettbl_Alerts(int id)
        {
            tbl_Alerts tbl_Alerts = await db.tbl_Alerts.FindAsync(id);
            if (tbl_Alerts == null)
            {
                return NotFound();
            }

            return Ok(tbl_Alerts);
        }

        // PUT: api/Alerts/5
        [ResponseType(typeof(void))]
        public async Task<IHttpActionResult> Puttbl_Alerts(int id, tbl_Alerts tbl_Alerts)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != tbl_Alerts.col_AlertID)
            {
                return BadRequest();
            }

            db.Entry(tbl_Alerts).State = EntityState.Modified;

            try
            {
                await db.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!tbl_AlertsExists(id))
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

        // POST: api/Alerts
        [ResponseType(typeof(tbl_Alerts))]
        public async Task<IHttpActionResult> Posttbl_Alerts(tbl_Alerts tbl_Alerts)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.tbl_Alerts.Add(tbl_Alerts);
            await db.SaveChangesAsync();

            return CreatedAtRoute("DefaultApi", new { id = tbl_Alerts.col_AlertID }, tbl_Alerts);
        }

        // DELETE: api/Alerts/5
        [ResponseType(typeof(tbl_Alerts))]
        public async Task<IHttpActionResult> Deletetbl_Alerts(int id)
        {
            tbl_Alerts tbl_Alerts = await db.tbl_Alerts.FindAsync(id);
            if (tbl_Alerts == null)
            {
                return NotFound();
            }

            db.tbl_Alerts.Remove(tbl_Alerts);
            await db.SaveChangesAsync();

            return Ok(tbl_Alerts);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool tbl_AlertsExists(int id)
        {
            return db.tbl_Alerts.Count(e => e.col_AlertID == id) > 0;
        }
    }
}