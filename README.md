This is a Google Cloud Function built to do a singular thing. Listen to Livepeer API for any video upload related events. These events are essential to the lifecycle of any video streaming platform. \n
Most of the apps see use daily outsource tasks like video formatting(mp4-ios-mobile) versions conversion, video to thumbnail, transcoding in general as thses tasks are colloquially called.
Livepeer is one the leading platform in this space. It lets developers use their wonderful apis to do the aformentioned tasks.
How I have used Livepeer is the primal discussion here!
I used Livepeer provided webhook functionality to setup a server that awakes when Livepeer wants to submit its given task of the time. I've made choice to use gcp cloud functions for its lightweightness.
I will write more about this in the future.
