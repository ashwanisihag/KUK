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
    
    public partial class tbl_Rating
    {
        public int col_RatingID { get; set; }
        public string col_UserID { get; set; }
        public short col_Rating { get; set; }
    
        public virtual AspNetUser AspNetUser { get; set; }
    }
}