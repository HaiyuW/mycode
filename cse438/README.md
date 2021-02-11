# Project Summary

## Group Member:
Haiyu Wang, Fan Wu, Ying Zeng

## Is there anything that you did that you feel might be unclear? Explain it here.
* Our PetHome App has a relatively slow loading speed. We don't know if we can use some tools to cache the data from the web API to speed up the loading procedure.

 ## Creative Portion
 * Web API Authorization
    * The dogpetapi and catpetapi require authorized key in the header when getting the data, so we use the @Header to include our keys.
    * The petfinderapi require authorized token to get data. Thus, we firstly use user key and password to get token using @FormUrlEncoded and @POST to get the token. Then we include the token into the header using @Header to pass the authentication. To handle the time out of the token, we refresh the token every time we attempt to get the data.

* Google Map
    * We include the google map in the activity of the ShelterInfoActivity.
    * To convert the actual address of the pets into latitude and longitude, we use Geocoding API to get the LatLng pair and use the pairs to locate the target pet in the Map.
    * Meanwhile, we need to encode the actual address when starting getting the data, so we use Uri.encode().

* Dial Interface
    * Users can click at the phone number and will navigate to the dial interface with the phone number showing at the dial bar.

