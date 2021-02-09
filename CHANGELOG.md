# Changelog

All notable changes to this project will be documented in this file. See [standard-version](https://github.com/conventional-changelog/standard-version) for commit guidelines.
## [18.0.0](https://github.com/Yelp/bento/compare/v17.0.0...v18.0.0) (2020-10-26)


### ⚠ BREAKING CHANGES

* **deps:** Bump RxJava2 to RxJava3.


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
