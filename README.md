ShowcaseViews
---

It's little fork/wrapper of great https://github.com/amlcurran/ShowcaseView
It allows showing multiple showcase views one after another.

Usage
====

Just like in classic ShowcaseView, to use ShowcaseViews use Builder pattern:

~~~
showcaseViews = new ShowcaseViews.Builder(this)
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setShotMode(ShowcaseViews.SHOT_MODE_MULTIPLE)
                    .addView(new ShowcaseViews.ViewProperties(
                            R.id.first_target,
                            "title of first target",
                            "content description of first target")
                    )
                    .addView(new ShowcaseViews.ViewProperties(
                            R.id.second_target,
                            "title of second target",
                            "content description of second target")
                    )
                    .setListener(new ShowcaseViews.ShowcaseViewsListener() {
                        @Override
                        public void onShowcaseStart() {
                            // do some stuff before first target shows
                        }

                        @Override
                        public void onShowcaseEnd(boolean hadViews) {
                            // do some stuff after last target shows
                        }
                    })
                    .show();
~~~

Copyright and Licensing
----

Copyright Alex Curran ([@amlcurran](https://twitter.com/amlcurran)) © 2012-2014. All rights reserved.

This library is distributed under an Apache 2.0 License.
