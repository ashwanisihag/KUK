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
using System.Web.Http.Cors;
using System.Web.Http.Description;
using Microsoft.AspNet.Identity;
using KUKWebApi;

namespace KUKWebApi.Controllers
{
    [Authorize]
    [EnableCors(origins: "*", headers: "*", methods: "*")]
    public class MessagesController : ApiController
    {
        private KUKEntities db = new KUKEntities();
        public class UpdateToken
        {
            public string token { get; set; }
            public string appVersion { get; set; }
        }

        [HttpPost]
        [ActionName("UpdateDeviceToken")]
        public HttpResponseMessage UpdateDeviceToken(UpdateToken token)
        {
            try
            {
                var userID = User.Identity.GetUserId();
                var isAdmin = User.IsInRole("Admin");
                var tbl_DeviceIds = db.tbl_DeviceIds.FirstOrDefault(d => d.col_UserID == userID);
                if (tbl_DeviceIds != null)
                {
                    tbl_DeviceIds.col_UserID = userID;
                    tbl_DeviceIds.col_DeviceToken = token.token;
                    tbl_DeviceIds.col_Version = token.appVersion;
                    tbl_DeviceIds.col_DateTime = DateTime.Now;
                    if (isAdmin)
                    {
                        tbl_DeviceIds.col_UserType = "Admin";
                    }
                    else
                    {
                        tbl_DeviceIds.col_UserType = "User";
                    }
                    db.SaveChanges();
                    return Request.CreateResponse(HttpStatusCode.OK, "Updated");
                }
                else
                {
                    tbl_DeviceIds ids = new tbl_DeviceIds();
                    ids.col_UserID = userID;
                    ids.col_DeviceToken = token.token;
                    ids.col_Version = token.appVersion;
                    if (isAdmin)
                    {
                        ids.col_UserType = "Admin";
                    }
                    else
                    {
                        ids.col_UserType = "User";
                    }
                    ids.col_DateTime = DateTime.Now;
                    db.tbl_DeviceIds.Add(ids);
                    db.SaveChanges();
                    return Request.CreateResponse(HttpStatusCode.OK, "Added");
                }
            }
            catch (Exception ex)
            {
                return Request.CreateResponse(HttpStatusCode.NotAcceptable, ex.Message);
            }
        }

        public class Notification
        {
            public string UserID { get; set; }
            public string Title { get; set; }
            public string Message { get; set; }
        }

        [HttpPost]
        [ActionName("SendNotification")]
        public HttpResponseMessage SendNotification(Notification notification)
        {
            LogApi.Log(User.Identity.GetUserId(), "GetUserMessages " + User.Identity.GetUserName() + " " + notification.Message);

            try
            {

                if (notification.Title.Contains("Chat"))
                {
                    var deviceTokens = db.tbl_DeviceIds.Where(d => d.col_UserID != notification.UserID);
                    if (deviceTokens != null)
                    {
                        foreach (var dt in deviceTokens)
                        {
                            Notifications.NotifyAsync(dt.col_DeviceToken, notification.Title, notification.Message);
                        }
                        return Request.CreateResponse(HttpStatusCode.OK, "Sent");
                    }
                    else
                    {
                        return Request.CreateResponse(HttpStatusCode.OK, "Failed");
                    }
                }
                else
                {

                    string deviceToken = db.tbl_DeviceIds.Where(d => d.col_UserID == notification.UserID).FirstOrDefault().col_DeviceToken;
                    if (!string.IsNullOrEmpty(deviceToken))
                    {
                        Notifications.NotifyAsync(deviceToken, notification.Title, notification.Message);
                        return Request.CreateResponse(HttpStatusCode.OK, "Sent");
                    }
                    else
                    {
                        return Request.CreateResponse(HttpStatusCode.OK, "Failed");
                    }
                }


            }
            catch (Exception ex)
            {
                return Request.CreateResponse(HttpStatusCode.NotAcceptable, ex.Message);
            }
        }
        // GET: api/Messages
        public IQueryable<tbl_UserMessages> Gettbl_Messages()
        {
            return db.tbl_UserMessages;
        }

        // GET: api/Messages/5
        [ResponseType(typeof(tbl_UserMessages))]
        public async Task<IHttpActionResult> Gettbl_Messages(int id)
        {
            tbl_UserMessages tbl_Messages = await db.tbl_UserMessages.FindAsync(id);
            if (tbl_Messages == null)
            {
                return NotFound();
            }

            return Ok(tbl_Messages);
        }
        public class GetMessageModel
        {
            public string UserID { get; set; }
            public int MessageID { get; set; }
            public string Message { get; set; }
            public string From { get; set; }
        }


        [HttpGet]
        [ActionName("GetUserMessages")]
        public IHttpActionResult Gettbl_UserMessages()
        {
            LogApi.Log(User.Identity.GetUserId(), "GetUserMessages " + User.Identity.GetUserName());

            try
            {
                var id = User.Identity.GetUserId();
                List<tbl_UserMessages> tbl_Messages = db.tbl_UserMessages.Where(m => m.col_ToUserID == id).ToList();
                if (tbl_Messages == null)
                {
                    return NotFound();
                }
                GetMessageModel getMessageModel;
                List<GetMessageModel> listGetMessageModel = new List<GetMessageModel>();
                foreach (var m in tbl_Messages)
                {
                    string firstName = db.AspNetUsers.Where(u => u.Id == m.col_FromUserID).Single().FirstName;
                    string lastName = db.AspNetUsers.Where(u => u.Id == m.col_FromUserID).Single().LastName;
                    var fromUser = firstName + " " + lastName;
                    getMessageModel = new GetMessageModel();
                    getMessageModel.UserID = m.col_FromUserID;
                    getMessageModel.Message = m.col_Message;
                    getMessageModel.MessageID = m.col_userMessagesID;
                    getMessageModel.From = fromUser;
                    listGetMessageModel.Add(getMessageModel);
                }
                if (listGetMessageModel.Count > 0)
                {
                    return Ok(listGetMessageModel);

                }
                else
                {
                    return Ok("No records found");
                }
            }
            catch (Exception ex)
            {
                return Ok("Error " + ex.Message);
            }
        }


        // PUT: api/Messages/5
        [ResponseType(typeof(void))]
        public async Task<IHttpActionResult> Puttbl_Messages(int id, tbl_UserMessages tbl_Messages)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != tbl_Messages.col_userMessagesID)
            {
                return BadRequest();
            }

            db.Entry(tbl_Messages).State = EntityState.Modified;

            try
            {
                await db.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!tbl_MessagesExists(id))
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

        public class MessageModel
        {
            public string Message { get; set; }
            public string userID { get; set; }
            public int parentMessageID { get; set; }
            public string groupID { get; set; }
        }
        public class StatusModel
        {
            public string Status { get; set; }
        }
        public class PurchaseStatusModel
        {
            public string PurchaseStatus { get; set; }
        }
        // POST: api/Messages
        [HttpPost]
        [ActionName("PostMessage")]
        public async Task<IHttpActionResult> Posttbl_Messages(MessageModel model)
        {
            LogApi.Log(User.Identity.GetUserId(), "PostMessage " + User.Identity.GetUserName() + " " + model.Message);

            try
            {
                var userId = User.Identity.GetUserId();
                //if (!ModelState.IsValid)
                //{
                //    return BadRequest(ModelState);
                //}
                tbl_UserMessages tbl_Messages = new tbl_UserMessages();
                tbl_Messages.col_DateTime = DateTime.Now;
                tbl_Messages.col_Message = model.Message;
                tbl_Messages.col_ToUserID = model.userID;
                tbl_Messages.col_FromUserID = userId;
                tbl_Messages.col_ParentMessageID = model.parentMessageID;
                db.tbl_UserMessages.Add(tbl_Messages);
                await db.SaveChangesAsync();

                try
                {
                    var token = db.tbl_DeviceIds.Where(d => d.col_UserID == model.userID).FirstOrDefault().col_DeviceToken;
                    string from = User.Identity.GetUserName();
                    Notifications.NotifyAsync(token, "Message from:" + from, model.Message);
                }
                catch (Exception ex)
                {

                }
                return Ok("Posted");
            }
            catch (Exception ex)
            {
                var userId = User.Identity.GetUserId();
                tbl_Log log = new tbl_Log();
                var user = db.AspNetUsers.Where(u => u.Id == userId).FirstOrDefault();
                log.col_UserID = userId;
                log.col_DateTime = DateTime.Now;
                log.col_Feature = ex.Message;
                db.tbl_Log.Add(log);
                db.SaveChanges();

                return Ok("Error " + ex.Message);
            }
            //return CreatedAtRoute("DefaultApi", new { id = tbl_Messages.col_MessageID }, tbl_Messages);
        }

        [HttpPost]
        [ActionName("PostStatus")]
        public async Task<IHttpActionResult> PostStatus(StatusModel model)
        {
            try
            {
                var tokens = db.tbl_DeviceIds;
                var userId = User.Identity.GetUserId();
                LogApi.Log(userId, "PostStatus " + User.Identity.GetUserName() + " " + model.Status);
                var partyStatus = db.tbl_PartyStatus.FirstOrDefault(d => d.col_UserID == userId && d.col_Date.Day == DateTime.Now.Day);
                if (partyStatus != null)
                {
                    partyStatus.col_Date = DateTime.Now;
                    partyStatus.col_IsAvailable = model.Status;
                    partyStatus.col_UserID = userId;
                    db.SaveChanges();
                    if (model.Status != "No")
                    {
                        foreach (var d in tokens)
                        {
                            Notifications.NotifyAsync(d.col_DeviceToken, "Get-together", "Get together: " + partyStatus.col_IsAvailable + "#" + User.Identity.GetUserName() + "#" + User.Identity.GetUserId());
                        }
                    }
                    return Ok("Updated");
                }
                else

                    partyStatus = new tbl_PartyStatus();
                partyStatus.col_Date = DateTime.Now;
                partyStatus.col_IsAvailable = model.Status;
                partyStatus.col_UserID = userId;
                db.tbl_PartyStatus.Add(partyStatus);
                await db.SaveChangesAsync();
                if (model.Status != "No")
                {
                    foreach (var d in tokens)
                    {
                        Notifications.NotifyAsync(d.col_DeviceToken, "Get-together", "Get together: " + partyStatus.col_IsAvailable + "#" + User.Identity.GetUserName() + "#" + User.Identity.GetUserId());
                    }
                }
                return Ok("Added");

            }
            catch (Exception ex)
            {
                return Ok(ex.Message);
            }
        }

        [HttpPost]
        [ActionName("PostPurchaseStatus")]
        public async Task<IHttpActionResult> PostPurchaseStatus(PurchaseStatusModel model)
        {
            try
            {
                var userId = User.Identity.GetUserId();
                var purchaseStatus = db.tbl_Purchase.FirstOrDefault(d => d.col_UserID == userId);
                if (purchaseStatus != null)
                {
                    purchaseStatus.col_PurchaseStatus = true;
                    purchaseStatus.col_UserID = userId;
                    db.SaveChanges();
                    return Ok("Updated");
                }
                else

                    purchaseStatus = new tbl_Purchase();
                purchaseStatus.col_PurchaseStatus = true;
                purchaseStatus.col_UserID = userId;
                db.tbl_Purchase.Add(purchaseStatus);
                await db.SaveChangesAsync();
                return Ok("Added");

            }
            catch (Exception ex)
            {
                return Ok(ex.Message);
            }
        }

        // DELETE: api/Messages/5
        [HttpDelete]
        [ActionName("DeleteMessage")]
        public async Task<IHttpActionResult> Deletetbl_Messages(int id)
        {
            LogApi.Log(User.Identity.GetUserId(), "DeleteMessage " + User.Identity.GetUserName());

            try
            {
                tbl_UserMessages tbl_Messages = await db.tbl_UserMessages.FindAsync(id);
                if (tbl_Messages == null)
                {
                    return NotFound();
                }

                db.tbl_UserMessages.Remove(tbl_Messages);
                await db.SaveChangesAsync();

                return Ok("Deleted");
            }
            catch (Exception ex)
            {
                return Ok("Error " + ex.Message);
            }
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool tbl_MessagesExists(int id)
        {
            return db.tbl_UserMessages.Count(e => e.col_userMessagesID == id) > 0;
        }
    }
}