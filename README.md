# FlickerApp
 A simple app for displaying images
 # Prerequisites
 Flicker API Key ,You can put the api key by changing API_KEY_PARAM member varible in MainActivityFragment 
 # Design 
  MVC
 # Libs used
 * Volley --> For Network Connections
 * Chrisbanes:PhotoView --> For Zooming
 * Picasso --> Photo Cashing and Download
 * Gson --> Json Parsing
 # Issues 
   I was using Git in a right way but somthing happened (by using git command or android )
  * InvalidVirtualFileAccessException: Accessing invalid virtual file: file://E:/Projects/Flickr/.git/refs/remotes; original:110930; found:116537
  * Invalid VCS root mapping
  * on git command error rejected-- 
   Searched many times but nothing found
   
 
# Chooises Desions 
* The Auto Refresh Work on Silence so if you want to 
     view Dialog Message uncomment the 3 lines above
* On Offline Mode Choose to Load Only 1 Page of Images
  as it will be not effecint to load all the pages
  but anyway if you want to add more photos 
   just send the page number to loadData(url)
   in a for loop where url=mUrl = String.format(BASEURL, METHOD_PARAM, API_KEY_PARAM, page, format, nojsoncallback);
   
