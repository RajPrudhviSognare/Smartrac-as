package com.experis.smartrac.as;


//API CALLS
public class Constants {

    public static String PREFERENCE_NAME = "SmartracNunhemsPreference";
    //public static String TAG_ATTENDANCE_TYPE = "In Time";

    public static boolean GEOFENCE_ENABLED = false;
    public static String ASSOCIATE_ID = "";
    public static String TL_ID = "";
    public static String od_from_time = "";
    public static String od_to_time = "";
    public static String client_name = "";
    public static String client_address = "";
    public static String OUTLET_ID = "";
    public static String ATTENDANCE_TYPE = "";
    public static String ATTENDANCE_IMAGE = "";
    public static String CURRENT_LAT = "0.0";
    public static String CURRENT_LONG = "0.0";
    public static String ATTENDANCE_DATE = "0000-00-00";
    public static String ATTENDANCE_TO_DATE = "0000-00-00";
    public static String REASON = "";
    public static String LEAVE_TYPE = "";
    public static String REMARKS = "";
    public static String DISTANCE = "0";


    public static String EMP_CODE = "EmpCode";
    public static String EMP_HEAD = "head";
    public static String SUB_HEAD = "SubHead";
    public static String DATE_RQST = "DateRqst";
    public static String BILL_FROM_DATE = "bill_from_date";
    public static String BILL_TO_DATE = "bill_to_date";
    public static String BILL_NUMBER = "bill_number";
    public static String BILL_DATE = "bill_date";
    public static String TRAVEL_MODE = "TravelMode";
    public static String CITY = "City";
    public static String JURNEY_FROM = "journey_from";
    public static String JURNEY_SART_TIME = "JStartTime";
    public static String JURNEY_TO = "journey_to";
    public static String JURNEY_END_TIME = "JEndTime";
    public static String TOLL_TAX = "TollTax";
    public static String TOLL_START_KM = "StartKM";
    public static String TOLL_END_KM = "EndKM";
    public static String TOTAL_JURNEY_KM = "Total_journey_km";
    public static String AMOUNT = "Amount";
    public static String HOTEL_NAME = "HotelName";
    public static String EMP_REMARKS = "remarks";
    public static String LIMIT = "Limit";
    public static String proofattach1 = "proofattach1";
    public static String proofattach2 = "proofattach2";

    public static String UNIV_LAT = "0.0";
    public static String UNIV_LONG = "0.0";
    public static String UNIV_RADIUS = "500.0";

    public static String UNIV_LAT1 = "0.0";
    public static String UNIV_LONG1 = "0.0";

    public static boolean FROM_PUSH = false;
    public static String PUSH_TYPE = "";
    public static String PUSH_FOR = "";
    public static String CURRENT_LOC = "Sodepur";
    public static String CURRENT_LOCCITY = "Kolkata";
    public static String CURRENT_LOCADD = "Sodepur, Kolkata-700110";

    ///////////////////////////////////

    //Local Server For Hu


    //Experis Server For Hu
	/*public static String BASE_URL = "http://10.194.5.12/projects10/smartrac_ceragon/index.php/api";
	public static String BASE_URL_CLIENT_LOGO = "http://10.194.5.12/projects10/smartrac_ceragon/uploads/client_logo/";
	public static String BASE_URL_ATTENDANCE_LOGO = "http://10.194.5.12/projects10/smartrac_ceragon/uploads/locations/";
*/
	/*//LIVE SERVER
	//http://smtrac.pairserver.com/luxour/index.php/login
	public static String BASE_URL = "http://smtrac.pairserver.com/ceragon/index.php/api";
	public static String BASE_URL_CLIENT_LOGO = "http://smtrac.pairserver.com/ceragon/uploads/client_logo/";
	public static String BASE_URL_ATTENDANCE_LOGO = "http://smtrac.pairserver.com/ceragon/uploads/locations/";*/

    //latest LIVE SERVER
    //http://gpil.smartrac.manpoweronline.in/login
    //http://gpil.smartrac.manpoweronline.in/
	/*public static String BASE_URL = "http://smtrac.pairserver.com/smartrac_gpil/index.php/api";
	public static String BASE_URL_CLIENT_LOGO = "http://smtrac.pairserver.com/smartrac_gpil/uploads/client_logo/";
	public static String BASE_URL_ATTENDANCE_LOGO = "http://smtrac.pairserver.com/smartrac_gpil/uploads/locations/";*/


    //http://smtrac.pairserver.com/basf/index.php/api/attendanceapi/attendanceEntryTest

    /*public static String BASE_URL = "http://smtrac.pairserver.com/smartrac_nunhems/index.php/api";
    public static String BASE_URL_CLIENT_LOGO = "http://smtrac.pairserver.com/smartrac_nunhems/uploads/client_logo/";
    public static String BASE_URL_ATTENDANCE_LOGO = "http://smtrac.pairserver.com/smartrac_nunhems/uploads/locations/";*/
    public static String BASE_URL = "http://nunhems.smartrac.manpoweronline.in/index.php/api";
    public static String BASE_URL_CLIENT_LOGO = "http://nunhems.smartrac.manpoweronline.in/uploads/client_logo/";
    public static String BASE_URL_ATTENDANCE_LOGO = "http://nunhems.smartrac.manpoweronline.in/uploads/locations/";


    public static String LOGIN_RELATIVE_URI = "/api/login";
    public static String FORGOT_PASSWORD_RELATIVE_URI = "/api/forgot_password";
    public static String CHANGE_PASSWORD_RELATIVE_URI = "/api/change_password";
    public static String ATTENDANCE_RELATIVE_URI = "/attendanceapi/attendanceEntryTest";
    public static String ASSOCIATE_REPORTS_ATTENDANCE_RELATIVE_URI = "/report/monthly_attendance";
    public static String ASSOCIATE_REPORTS_ATTENDANCE_MONTHLY_RELATIVE_URI = "/report/getDetailsmonthlyReport";
    public static String ATTENDANCE_APPROVAL_RELATIVE_URI = "/attendanceapi/getAttendanceByTl";
    public static String ATTENDANCE_APPROVALREJECTION_RELATIVE_URI = "/attendanceapi/setApprovedAttendance";
    public static String GET_ATTENDANCE_COUNT_DASHBOARD_RELATIVE_URI = "/report/getAttendanceCountDashboard";
    public static String GET_ABSCONDING_COUNT_DASHBOARD_RELATIVE_URI = "/report/get3daysNoAttendanceListApp";
    public static String GETASSOCIATE_BY_TEAMLEAD_RELATIVE_URI = "/message/getAssociateByTl";
    public static String SENDMESSAGE_BY_TL_RELATIVE_URI = "/message/setMessage";
    public static String GETMESSAGE_BY_ASSOCIATE_RELATIVE_URI = "/message/getMessageByAssociate";
    public static String GETSENTMESSAGE_BY_TL_RELATIVE_URI = "/message/getSendMessageByTl";
    //public static String GETASSOCIATE_BY_TEAMLEAD_RELATIVE_URI = "/message/getAssociateByTl";

    /*public static String SALES_TRACKING_GETPRODUCT_BYIMEI_RELATIVE_URI = "/sales_tracking/getProductByImie";
    public static String SALES_TRACKING_SET_SELLINFO_RELATIVE_URI = "/sales_tracking/setSalesInfo";
    public static String ASSOCIATE_REPORTS_ATTENDANCE_RELATIVE_URI = "/report/monthly_attendance";
    public static String ASSOCIATE_REPORTS_TARGETACHIEVEMENT_RELATIVE_URI = "/report/target_vs_achievements";
    public static String ASSOCIATE_REPORTS_ATTENDANCE_MONTHLY_RELATIVE_URI = "/report/getDetailsmonthlyReport";
    public static String GETCOMPETITORLIST_RELATIVE_URI = "/sales_tracking/getCompetitorList";
    public static String SETCOMPETITORSALES_RELATIVE_URI = "/sales_tracking/setCompetitorSales";
    public static String GETCATEGORY_RELATIVE_URI = "/stock/getCategory";
    public static String GETSUBCATEGORY_RELATIVE_URI = "/stock/getSubCategory";
    public static String GETPRODUCT_RELATIVE_URI = "/stock/getProducts";
    public static String GETPRODUCT_INFO_RELATIVE_URI = "/stock/getproductDtls";
    public static String SETPRODUCT_IMEI_RELATIVE_URI = "/stock/insProductImei";
    public static String GETASSOCIATE_BY_TEAMLEAD_RELATIVE_URI = "/target/getAssociateByTl";
    public static String GETPRODUCTTARGET_BY_ASSOCIATE_RELATIVE_URI = "/target/getProductsTargetBYAssociate";
    public static String SETPRODUCTTARGET_BY_ASSOCIATE_RELATIVE_URI = "/target/setProductsTargetBYAssociate";
    public static String SENDMESSAGE_BY_TL_RELATIVE_URI = "/message/setMessage";
    public static String GETMESSAGE_BY_ASSOCIATE_RELATIVE_URI = "/message/getMessageByAssociate";
    public static String GETSENTMESSAGE_BY_TL_RELATIVE_URI = "/message/getSendMessageByTl";*/
    public static String LEAVESTATUS_RELATIVE_URI = "/api/check_leave";

    /*public static String GET_ATTENDANCE_COUNT_DASHBOARD_RELATIVE_URI = "/report/getAttendanceCountDashboard";
    public static String GET_ATTENDANCE_COUNT_DASHBOARD_NOT_AVAILABLE_RELATIVE_URI = "/report/getNotAvailableListApp";
    public static String GET_ATTENDANCE_COUNT_DASHBOARD_ABSCONDING_RELATIVE_URI = "/report/get3daysNoListApp";

    public static String SET_KYC_DETAILS_RELATIVE_URI = "/profile/insProfile";*/
    public static String CLIENT_ID = "7235";
    //public static String base_url_default = String.format("http://outbound.manpoweronline.in/ess/ess.asmx?wsdl");
    public static String base_url_default = String.format("http://outbound.manpoweronline.in/essv3/ess.asmx?wsdl");
    public static String base_url_default2 = String.format("http://outbound.manpoweronline.in/essv4/ess.asmx?wsdl");
    public static String base_url_default1 = String.format("http://outbound.manpoweronline.in/attendance/attlogv3/attendance.asmx?wsdl");

    public static final String soapRequestHeader = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">";

    /////////////////////////////////

}