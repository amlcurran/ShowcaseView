The new ShowcaseView (v5.0)
====

This library has become more popular than I ever intended - but at the same time more unwieldy.
This version is intended to be more stable,
more friendly and better tested.

There **are** features missing compared to v4. If you'd like to keep using them, then use the **legacy** branch in this repository. Please see the bottom of this page for more information.

ShowcaseView library
====
  
The ShowcaseView library is designed to highlight and showcase specific parts of apps to the user with a distinctive and attractive overlay. This library is great for pointing out points of interest for users, gestures, or obscure but useful items.

The library is based on the "Cling" view found in the Launcher on Ice-Cream Sandwich and Jelly Bean, but extended to be easier to use.

Please check out [the website](http://amlcurran.github.io/ShowcaseView/) for more information.

<img src='./example@2x.png' width='270' height='480' />
<img src='./example2@2x.png' width='270' height='480' />

Set-up
----

The library is now only compatible with Gradle out of the box. To use this library in your project, either:

* Add the dependency from Maven like so: `compile 'com.github.amlcurran.showcaseview:library:5.0.0'`. You'll also need to add Maven central to your `repositories {}` tag like so:
~~~
repositories {
    mavenCentral()
}
~~~
* Copy the library project into your Gradle project. In your settings.gradle, add the project (using the `include (':libary')` notation). Sync Gradle and then go ahead! 

Is it worth using?
----
Perhaps. Why not ask 
[Google](https://github.com/googlecast/CastVideos-android),
[HaxSync](https://play.google.com/store/apps/details?id=org.mots.haxsync), or [AllCast](https://play.google.com/store/apps/details?id=com.koushikdutta.cast), which each use the library?

Usage
----

To use ShowcaseView, use the Builder pattern.

As an example:

~~~
new ShowcaseView.Builder(this)
    .setTarget(new ActionViewTarget(this, ActionViewTarget.Type.HOME))
    .setContentTitle("ShowcaseView")
    .setContentText("This is highlighting the Home button")
    .hideOnTouchOutside()
    .build();
~~~ 

What's new in v5
---
* Gradle and Maven compatibility! For now only snapshots are available, which can be accessed and added to Gradle builds by adding `compile 'com.github.amlcurran.showcaseview:library:5.0.0-SNAPSHOT'` to your dependencies. You will require the snapshot repository to be defined in your repositories.
* Buidler pattern. It was much too difficult to create and set up a ShowcaseView, so there is now a Builder pattern available. This makes it dead easy to create a ShowcaseView.
* New KitKat style showcase. Currently, this is only available by creating a Builder using the `new Builder(activity, true)` constructor.

What's missing in v5
---

- ShowcaseViews: the class which queues up ShowcaseViews in a tutorial-type method. I never
really liked this class (generally, you should use SCV sparingly); I'll add it back in based on
the Builder class when I can.
- Ghostly hand: this has gone for now until I can test-drive it back in.
- Scale multiplier: this has gone for simplicity - if people really loved it I'll add in back in

FAQs
---

**Where has X feature gone?**

Look one paragraph up!

**Waaaah, but I really liked feature X!!!**

Switch to the legacy branch and use that one then! All legacy features are in there.

**What happened to all the other constructors?**

Gone. You should be using the new Target API.

**What if I want to add feature X?**

At the moment, I'm not taking any feature requests. It's unlikely I'll take many anyway,
unless I feel they are both useful and well tested. If you have some cosmetic tweak then I don't
want that added into the library as *another* option. But, if you need to make a tweak to the
library to add such a tweak to your own, overridden ShowcaseView then that is totally great.


Copyright and Licensing
----

Copyright Alex Curran ([+Alex](https://plus.google.com/110510888639261520925/posts)) © 2012. All rights reserved.

This library is distributed under an Apache 2.0 License.
