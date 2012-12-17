Showcase View library  
====
  
The ShowcaseView library is designed to highlight and showcase specific parts of apps to the user with a distinctive and attractive overlay. This library is great for pointing out points of interest for users, gestures, or obscure but useful items.

The library is based on the "Cling" view found in the Launcher on Ice-Cream Sandwich and Jelly Bean, but extended to be easier to use.

![Example image](https://raw.github.com/Espiandev/ShowcaseView/master/example.png)

Usage
----

ShowcaseView v2 is nearly fully automated, providing a much easier experience adding the library to your apps. Now, to add a ShowcaseView to an Activity, simply call one of the `ShowcaseView.insertShowcaseView(...)` functions. These functions return the ShowcaseView initialised, and so can undergo further modification. To customise the ShowcaseView further, you can supply `insertShowcaseView(...)` with a `ShowcaseView.Config` argument which can customise:
- Whether to block touches on the ShowcaseView
- Show or hide an "OK" button, which hides the ShowcaseView
- Change whether the ShowcaseView is used only once, or multiple times (`TYPE_SINGLE_SHOT` or `TYPE_NO_LIMIT`)
- Where to insert the ShowcaseView in the heirarchy (`INSERT_TO_CONTENT` will not cover the ActionBar; `INSERT_TO_DECOR` will)

Gestures can now be indicated using `animateGesture(...)`, which returns a [NineOldAndroids](http://nineoldandroids.com) `AnimatorSet`, which can be gestured simply by calling `start()` on it.

Styles are included to maintain consistently in ShowcaseViews. Buttons should use the style ClingButton, with title text using ClingTitleText and standard text using ClingText.

Upcoming features
----

At the moment, this library is quite bare. However, I hope to add the following features very soon:
- ~~Support for showcasing raw co-ordinates~~ _Use `setShowcasePosition(x,y)`_
- Support for showcasing ActionBar items
- Variable sized circles for showcasing
- ~~Easier usage~~ _This is what v2 is for!_
- ~~On-demand showing and hiding of ShowcaseView~~ _Use `show()` and `hide()`_

Copyright and Licensing
----

Copyright Alex Curran ([+Alex](https://plus.google.com/110510888639261520925/posts)) © 2012. All rights reserved.

This library is disributed under an Apache 2.0 License.
