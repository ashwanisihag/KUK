using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using System.Web;

namespace KUKWebApi
{
    public static class Notifications
    {
        public static async Task<bool> NotifyAsync(string to, string title, string body)
        {
            try
            {
                // Get the server key from FCM console
                var serverKey = string.Format("key={0}", "AAAAOJbKTP0:APA91bEqJ1VP7fCqMPKZ1jj9s_NIz8foBIAjkAcL3wYbXzfRRHV7yMiLYlpe1qfE1IObYJg1FjBCiTji34_Q7EBWZO1pHwI95c6LH8Y9ZH_oDRTYH2zAZCRHeNBIilzBJEraJzR5PFpnF174aPjyO8vTVgBjscrc5A");

                // Get the sender id from FCM console
                var senderId = string.Format("id={0}", "243048008957");

                //var data = new
                //{
                //    to, // Recipient device token
                //    notification = new { title, body }
                //};
                var data = new
                {
                    to,
                    notification = new
                    {
                        notification = new { title, body, sound = "Enabled" }
                        
                    },
                    data = new
                    {
                        data  =new { title, body, sound = "Enabled" }
                    }
                };
            
                // Using Newtonsoft.Json
                var jsonBody = JsonConvert.SerializeObject(data);

                using (var httpRequest = new HttpRequestMessage(HttpMethod.Post, "https://fcm.googleapis.com/fcm/send"))
                {
                    httpRequest.Headers.TryAddWithoutValidation("Authorization", serverKey);
                    httpRequest.Headers.TryAddWithoutValidation("Sender", senderId);
                    httpRequest.Content = new StringContent(jsonBody, Encoding.UTF8, "application/json");

                    using (var httpClient = new HttpClient())
                    {
                        var result = await httpClient.SendAsync(httpRequest);

                        if (result.IsSuccessStatusCode)
                        {
                            return true;
                        }
                        else
                        {
                            // Use result.StatusCode to handle failure
                            // Your custom error handler here
                        }
                    }
                }
            }
            catch (Exception ex)
            {
            }

            return false;
        }
    }
}