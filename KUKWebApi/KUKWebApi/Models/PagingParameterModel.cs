using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace KUKWebApi.Models
{
    public class PagingParameterModel
    {
        const int maxPageSize = 100;

        public int pageNumber { get; set; } = 1;

        public int _pageSize { get; set; } = 100;

        public int pageSize
        {

            get { return _pageSize; }
            set
            {
                _pageSize = (value > maxPageSize) ? maxPageSize : value;
            }
        }
    }
}