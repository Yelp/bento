# Changelog

All notable changes to this project will be documented in this file. See [standard-version](https://github.com/conventional-changelog/standard-version) for commit guidelines.

## [21.0.0](https://github.com/Yelp/bento/compare/v20.0.0...v21.0.0) (2023-09-14)


### ⚠ BREAKING CHANGES

* Bump AGP 8.0.2, Gradle 8.3.0, Kotlin 1.8.21, Prod & Test Libs to latest

### Features

* Bump AGP 8.0.2, Gradle 8.3.0, Kotlin 1.8.21, Prod & Test Libs to latest ([47adaeb](https://github.com/Yelp/bento/commit/47adaeb4d41567b547c0a397b80774ca039da5c7))
* install java 17 ([ea8563a](https://github.com/Yelp/bento/commit/ea8563ae2264263a9d2f33e3ad7a2b34d83fd659))


### Bug Fixes

* Compose view holder fails to update correctly when the component state changes ([ba47ad0](https://github.com/Yelp/bento/commit/ba47ad0ff7e556b6e10efb97a8dbe7f7eec9e4cc))
* Fix crash in ToggleScrollInRecyclerViewActivity.kt ([5028b14](https://github.com/Yelp/bento/commit/5028b1407bd2a5c177bfe7942d4300a09b10394f))

## [20.0.0](https://github.com/Yelp/bento/compare/v19.1.0...v20.0.0) (2023-04-04)


### ⚠ BREAKING CHANGES

* compileSdkVersion and targetSdkVersion to 33, AGP 7.4.2, Gradle 7.5.1, Kotlin 1.8.10, Compose 1.4.0, Compose Kotlin Compiler 1.4.4, Kotlin 1.8.10, RecyclerView 1.3.0; No need to set ViewCompositionStrategy any more

### Features

* bump libs for Compose/XML interop performance win since RecyclerView 1.3.0; move composeView.setContent to bind; ([08d1ada](https://github.com/Yelp/bento/commit/08d1adac87643574090cc4614d5743ee26672dd5))

## [19.1.0](https://github.com/Yelp/bento/compare/v19.0.0...v19.1.0) (2022-12-08)


### Features

* Add ViewPager2ComponentController and tests ([679ae82](https://github.com/Yelp/bento/commit/679ae828dacd3a025b1893cf3276352538b2f8ed))


### Bug Fixes

* **publishing:** Fixing publishing to Maven Central ([ae96b38](https://github.com/Yelp/bento/commit/ae96b38f69c6bd5810ddc0a22bafa70a47a2e7e7))

## [19.0.0](https://github.com/Yelp/bento/compare/v18.1.2...v19.0.0) (2022-04-27)


### ⚠ BREAKING CHANGES

* Increasing the minSDK and targetSDK may mean this version is no longer compatible with some apps.

### Features

* Added jetpack compose support and updated build tools and libraries. ([7c8f6d6](https://github.com/Yelp/bento/commit/7c8f6d61ec2fa6a552deedc28a6f4c00985036fb))
* Added jetpack compose support and updated build tools and libraries. ([ef34192](https://github.com/Yelp/bento/commit/ef34192c22f289139ce7eb7a91f945c42eb4cb93))
* Added smarter async inflation and limited the max number of inflated views in some cases. ([4bd193c](https://github.com/Yelp/bento/commit/4bd193c1691f23d0a53c834a61786082287e1da4))


### Bug Fixes

* Fixed a few bugs in the smart async implementation. ([eead220](https://github.com/Yelp/bento/commit/eead2200e5a8e05b3cba36a3766ee595804325fe))

### [18.2.1](https://github.com/Yelp/bento/compare/v18.2.0...v18.2.1) (2021-10-06)


### Bug Fixes

* Fixed a few bugs in the smart async implementation. ([eead220](https://github.com/Yelp/bento/commit/eead2200e5a8e05b3cba36a3766ee595804325fe))

## [18.2.0](https://github.com/Yelp/bento/compare/v18.1.2...v18.2.0) (2021-09-23)


### Features

* Added smarter async inflation and limited the max number of inflated views in some cases. ([4bd193c](https://github.com/Yelp/bento/commit/4bd193c1691f23d0a53c834a61786082287e1da4))

### [18.1.2](https://github.com/Yelp/bento/compare/v18.1.1...v18.1.2) (2021-08-19)


### Bug Fixes

* Disabled async bento for the CarouselComponent. ([ed2faa2](https://github.com/Yelp/bento/commit/ed2faa291a74ce12682e159500a22054ec5b76ee))

### [18.1.1](https://github.com/Yelp/bento/compare/v18.1.0...v18.1.1) (2021-08-12)


### Bug Fixes

* Fixed a memory leak in the async inflation feature. ([5bd8020](https://github.com/Yelp/bento/commit/5bd80207a492adb32c7d5cea1cead8eadfb8fde9))

## [18.1.0](https://github.com/Yelp/bento/compare/v18.0.2...v18.1.0) (2021-07-20)


### Features

* Inflate views on a background thread. ([7e8e908](https://github.com/Yelp/bento/commit/7e8e9080d381c280ed1b1fe7165b1c03b18e488b))

### [18.0.2](https://github.com/Yelp/bento/compare/v18.0.1...v18.0.2) (2021-05-27)


### Bug Fixes

* Upgrade Guava to 28.1 ([92f74b5](https://github.com/Yelp/bento/commit/92f74b5653356cfbfc6bccf9666c37a4e684591e))

## [18.0.1](https://github.com/Yelp/bento/compare/v18.0.0...v18.0.1) (2021-02-10)


### Bug Fixes

* Fix crash in ComponentVisibilityListener ([6133a75](https://github.com/Yelp/bento/commit/6133a7553187ec92ab67300b74687b28e2ea828f))

## [18.0.0](https://github.com/Yelp/bento/compare/v17.0.0...v18.0.0) (2020-10-26)


### ⚠ BREAKING CHANGES

* Migrated to RxJava3. ([#66](https://github.com/Yelp/bento/issues/66)) ([1fc25bc](https://github.com/Yelp/bento/commit/1fc25bcc4ecaa91e66defa46a02d24f5a9124d67))


### [17.0.1](https://github.com/Yelp/bento/compare/v17.0.0...v17.0.1) (2021-02-10)


### Bug Fixes

* Fix crash in ComponentVisibilityListener ([df54e68](https://github.com/Yelp/bento/commit/df54e68ee7d953116f3467ef95608d0e74b55d49))

## [17.0.0](https://github.com/Yelp/bento/compare/v16.0.0...v17.0.0) (2020-12-14)


### ⚠ BREAKING CHANGES

* projects need to be fully migrated to AndroidX to use Bento

* drop jetifier ([a90291b](https://github.com/Yelp/bento/commit/a90291b8582db62a5cc7ed49dd3614722d603fc2))

## [16.0.0](https://github.com/Yelp/bento/compare/v15.8.0...v16.0.0) (2020-05-07)


### ⚠ BREAKING CHANGES

* **deps:** Bump kotlin and java

### Bug Fixes

* **carousel:** Fix CarouselComponent recycling behavior ([ee38d72](https://github.com/Yelp/bento/commit/ee38d728cf99f73eff7417ea8a8a428ea87ff11b)), closes [#62](https://github.com/Yelp/bento/issues/62)


* **deps:** Bump kotlin and java ([4a846ff](https://github.com/Yelp/bento/commit/4a846ff17eef0e3db9433d5bfb9a68232ed71de2))

## [15.8.0](https://github.com/Yelp/bento/compare/v15.7.0...v15.8.0) (2020-03-31)


### Features

* Add an isScrollable boolean flag to ComponentController ([afdf6ae](https://github.com/Yelp/bento/commit/afdf6aecf3501897f1c5ab7b266570bf9c4afa18)), closes [#36](https://github.com/Yelp/bento/issues/36)


### Bug Fixes

* nested scrolling in view pager controller ([99765d2](https://github.com/Yelp/bento/commit/99765d29365ea1b7ce79afaaef7c8bdfe7968daa))

# Bento Releases
## Version 15.7.0
_2020-02-05_
* New: Add a registerItemVisibilityListener to Component, to avoid needing to override components simply for that. (Comes bundled with an unregister method)
* Fix: Change CarouselComponent behavior so that it notifies it's nested Components when it becomes visible or invisible because of scrolling.

## Version 15.6.0
_2020-01-27_
* Fix: Change NestedComponent to have separate presenter from its inner Component.

## Version 15.5.0
_2020-01-16_
* New: Add remove() and clear() functions to CarouselComponent.
* New: Add a unit test for testing CarouselComponent's add(), addAll(), remove(), and clear() functions.

## Version 15.4.0
_2019-12-18_
* New: Add a NestedComponent that can be used for nesting a given inner Component inside a given outer ViewHolder.

## Version 15.3.2
_2019-10-08_
* Fix: Fixed bug in ListItemTouchCallback causing crash when viewHolder.adapterPosition is -1
* Fix: Add detailed exception thrown when AccordionList.rangedValueAt is called with an incorrect value.

## Version 15.3.1
_2019-08-29_
* Fix: Fixed appending data to ListComponent.
* Fix: Fixed issue where we were no longer sharing the RecyclePool in CarouselComponent.
* Fix: Fixed unit and espresso tests. No more PowerMock.

## Version 15.3.0
_2019-08-13_
* Behavior change: CarouselComponent now remembers its scroll position when it's scrolled offscreen.

## Version 15.2.0
_2019-08-07_
* New: Added TabViewPagerComponent to use both TabLayout and ViewPager in one view.

## Version 15.1.1
_2019-07-11_
* Fix: Add check for gap in ComponentGroup ([#20](https://github.com/Yelp/bento/pull/20))

## Version 15.1.0
_2019-07-11_
* New: Add support for drag and drop for RecyclerViews.

## Version 15.0.0
_2019-05-20_
* Behavior change: ListComponent callback for onItemVisible will only be called on items, not on separators.

## Version 14.1.0 
_2019-05-03_

* Behavior change: when inserting a component or item at index 0, the RecyclerViewComponentController will keep the RecyclerView scrolled on top if it was currently showing the item at index 0.

## Version 14.0.0
_2019-04-18_

* First official Bento public release.
