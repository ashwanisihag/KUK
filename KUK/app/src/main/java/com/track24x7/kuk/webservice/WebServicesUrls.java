package com.track24x7.kuk.webservice;

/**
 * Created by Ashwani Sihag on 20-01-2017.
 */

public class WebServicesUrls {

    public static final String BASE_URL = "http://www.auggi.com:1956/";
    public static final String STORAGE_PATH_UPLOADS = "uploads/";
    public static final String DATABASE_PATH_UPLOADS = "uploads";
    public static final String GET_THUMBNAILS =BASE_URL + "api/AspNetUsers/GetPhotoList";
    public static final String GET_PHOTO =BASE_URL + "api/AspNetUsers/GetAlbumThumbnail/";
    public static final String GET_PHOTO_FULL =BASE_URL + "api/AspNetUsers/GetAlbumPhoto/";
    public static final String TOKEN = BASE_URL + "Token";
    public static final String REGISTER_USER = BASE_URL + "api/Account/Register";
    public static final String UPDATE_USER = BASE_URL + "api/AspNetUsers/UpdateUser";
    public static final String POST_PURCHASE_STATUS= BASE_URL + "api/Messages/PostPurchaseStatus";
    public static final String POST_STATUS= BASE_URL + "api/Messages/PostStatus";
    public static final String MESSAGE_USER = BASE_URL + "api/Messages/PostMessage";
    public static final String NOTIFICATION_USER = BASE_URL + "api/Messages/SendNotification";
    public static final String USER_MESSAGES = BASE_URL + "api/Messages/GetUserMessages";
    public static final String DELETE_MESSAGES = BASE_URL + "api/Messages/DeleteMessage";
    public static final String POST_NEWS = BASE_URL + "api/News/PostNews";
    public static final String GET_NEWS = BASE_URL + "api/News/GetNews";
    public static final String DELETE_NEWS = BASE_URL + "api/News/DeleteNews";

    public static final String POST_JOB = BASE_URL + "api/Jobs/PostJob";
    public static final String GET_JOBS = BASE_URL + "api/Jobs/GetJobs";
    public static final String GET_MY_JOBS = BASE_URL + "api/Jobs/GetMyJobs";
    public static final String DELETE_JOB= BASE_URL + "api/Jobs/DeleteJob";

    public static final String POST_BUSINESS = BASE_URL + "api/BusinessDirectory/PostBusiness";
    public static final String GET_BUSINESS = BASE_URL + "api/BusinessDirectory/GetBusiness";
    public static final String GET_MY_BUSINESS = BASE_URL + "api/BusinessDirectory/GetMyBusiness";
    public static final String DELETE_BUSINESS= BASE_URL + "api/BusinessDirectory/DeleteBusiness";

    public static final String POST_REQUIREMENT = BASE_URL + "api/Requirements/PostRequirement";
    public static final String GET_REQUIREMENT = BASE_URL + "api/Requirements/GetRequirement";
    public static final String GET_MY_REQUIREMENT = BASE_URL + "api/Requirements/GetMyRequirement";
    public static final String DELETE_REQUIREMENT= BASE_URL + "api/Requirements/DeleteRequirement";

    public static final String ALL_USERS = BASE_URL + "api/AspNetUsers/GetMembers";
    public static final String ONLINE_MEMBERS = BASE_URL + "api/AspNetUsers/GetOnlineMembers";
    public static final String ALL_DUPLICATES = BASE_URL + "api/AspNetUsers/GetDuplicateMembers";
    public static final String BIRTHDAYS = BASE_URL + "api/AspNetUsers/GetBirthdayMembers";
    public static final String IMAGE_URL = BASE_URL + "api/AspNetUsers/UserImage/";
    public static final String POST_FILE = BASE_URL + "api/AspNetUsers/PostFileUpload";
    public static final String POST_ALBUM_FILE = BASE_URL + "api/AspNetUsers/PostAlbumPhotoUpload";
    public static final String UPDATE_PARTY_LOCATION = BASE_URL + "api/AspNetUsers/UpdatePartyUserLocation";
    public static final String UPDATE_LOCATION = BASE_URL + "api/AspNetUsers/UpdateUserLocation";
    public static final String ACTIVATE_USER = BASE_URL + "api/AspNetUsers/ActivateUser";
    public static final String SEARCH = BASE_URL + "api/AspNetUsers/SearchMembers";
    public static final String GET_STATES_OF_INDIA = "http://appentus.me/mk/appiqo_bid/SellerRegistration/getAllStatesofIndia";
    public static final String GET_USER = BASE_URL +  "api/AspNetUsers/GetUser/";
    public static final String FORGOT_PASSWORD =BASE_URL +  "api/Account/ForgotPassword";
    public static final String UPDATE_ID = BASE_URL +  "api/Messages/UpdateDeviceToken";
    public static final String DELETE_USER= BASE_URL + "api/AspNetUsers/DeleteUser";
    /*   public static final String REGISTRATION = BASE_URL + "api/Account/RegistrationTry";
       public static final String ALERTS = BASE_URL +  "api/AspNetUsers/Alerts";*/
    public static final String GET_VERSION = BASE_URL +  "api/AspNetUsers/GetVersion";
    public static String formAllUserUrl(String page_number, String page_size,String Institute, String leaving_year) {
        return ALL_USERS + "?pageNumber=" + page_number + "&pageSize=" + page_size + "&leavingYear=" + leaving_year;
    }


}
