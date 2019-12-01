using System;
using System.Data;
using System.Linq;
using Microsoft.AspNet.Identity;
namespace KUKWebApi
{
    public static class LogApi
    {


        public static void Log(String userId,String logString)
        {
            try
            {
                using (KUKEntities db = new KUKEntities())
                {
                    tbl_Log log = new tbl_Log();
                    var user = db.AspNetUsers.Where(u => u.Id == userId).FirstOrDefault();
                    log.col_UserID = userId;
                    log.col_DateTime = DateTime.Now;
                    log.col_Feature = logString;
                    db.tbl_Log.Add(log);
                    db.SaveChanges();
                }
            }
            catch (Exception ex)
            {

            }
        }

    }
}