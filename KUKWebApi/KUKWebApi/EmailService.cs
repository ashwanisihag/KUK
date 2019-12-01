using Microsoft.AspNet.Identity;
using SendGrid;
using SendGrid.Helpers.Mail;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;
using System.Web;

namespace KUKWebApi
{
    public class EmailService : IIdentityMessageService
    {
        public async Task SendAsync(IdentityMessage message)
        {        
            var client = new SendGridClient("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");
            var from = new EmailAddress("sainikschoolkunjpura2019@gmail.com","Kunjians Admin");
            var subject = "Recover Password";
            var to = new EmailAddress(message.Destination,"Kunjian");
            var htmlContent = "<strong>" + message.Body  +"</strong>";;
            var msg = MailHelper.CreateSingleEmail(from, to, subject, message.Body, htmlContent);
            var response =  await client.SendEmailAsync(msg);
        }
    }
}