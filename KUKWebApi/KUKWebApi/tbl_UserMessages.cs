//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace KUKWebApi
{
    using System;
    using System.Collections.Generic;
    
    public partial class tbl_UserMessages
    {
        public int col_userMessagesID { get; set; }
        public string col_Message { get; set; }
        public System.DateTime col_DateTime { get; set; }
        public Nullable<int> col_ParentMessageID { get; set; }
        public string col_ToUserID { get; set; }
        public string col_FromUserID { get; set; }
    
        public virtual AspNetUser AspNetUser { get; set; }
    }
}