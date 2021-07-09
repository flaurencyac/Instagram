# Project 4 - *Instagram*

**Instagram** is a photo sharing app using Parse as its backend.

Time spent: **32** hours spent in total

## User Stories

The following **required** functionality is completed:

- [x] User sees app icon in home screen.
- [x] User can sign up to create a new account using Parse authentication
- [x] User can log in to his or her account
- [x] The current signed in user is persisted across app restarts
- [x] User can log out of his or her account
- [x] User can take a photo, add a caption, and post it to "Instagram"
- [x] User can view the last 20 posts submitted to "Instagram"
- [x] User can pull to refresh the last 20 posts submitted to "Instagram"
- [x] User can tap a post to go to a Post Details activity, which includes timestamp and caption.
- [x] User sees app icon in home screen

The following **stretch** features are implemented:

- [x] Style the login page to look like the real Instagram login page.
- [x] Style the feed to look like the real Instagram feed.
- [x] User can load more posts once he or she reaches the bottom of the feed using endless scrolling.
- [x] User should switch between different tabs using fragments and a Bottom Navigation View.
  - [x] Feed Tab (to view all posts from all users)
  - [x] Capture Tab (to make a new post using the Camera and Photo Gallery)
  - [x] Profile Tab (to view only the current user's posts, in a grid)
- [x] Show the username and creation time for each post
- User Profiles:
  - [x] Allow the logged in user to add a profile photo
  - [x] Display the profile photo with each post
  - [x] Tapping on a post's username or profile photo goes to that user's profile page
  - [x] User Profile shows posts in a grid
- [ ] After the user submits a new post, show an indeterminate progress bar while the post is being uploaded to Parse
- [x] User can comment on a post and see all comments for each post in the post details screen.
- [x] User can like a post and see number of likes for each post in the post details screen.

The following **additional** features are implemented:

- [ ] List anything else that you can get done to improve the app functionality!
- [x] User can toggle the password visibility on or off
- [x] Fragment Dialog modal overlay for the composition of comments
- [x] User can see the details of any of their posts from their profile fragment 

Please list two areas of the assignment you'd like to **discuss further with your peers** during the next class (examples include better ways to implement something, how to extend your app in certain ways, etc):

1. How to best design the schema for Parse server backend (better way to implement finding out if the current user liked a post where instead of having a list of like object with pointers to user and post, it would be better and more efficient to have each post have a list of user IDs of users who have liked the post)
2. I would extend my app by improving the UI more with making the Floating Action Button for composing comments disappear when the user swipes up in the comments recycler view
3. I would also make it so that a user could compose a comment in the timeline and see it propagate in the details screen of the post

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='https://github.com/flaurencyac/Instagram/blob/main/Instagram.gif' width='' alt='Video Walkthrough' />

GIF created with Record It

## Credits

List an 3rd party libraries, icons, graphics, or other assets you used in your app.

- [Android Async Http Client](http://loopj.com/android-async-http/) - networking library


## Notes

Some bugs I ran into:
I had some difficulty designing the schema of the Parse server for making sure that the status of a user's likes persist because I found out through using log statements that when the like doesn't persist it was because of the fact that the view (for the like button) was binded before the query to the server was finished. To solve this I have to create a property of the post class that is an array of user objects, this array of user objects would signify the users that liked the post. While the posts are queried I can include the array of user objects as something I wish to include in my query, which is a better and more efficient solution to the current schema I have where posts do not have references to the likes on it.

Android studio doesn't accept if a model class has two decorators (one for Parcel and one for ParseObject, because it doesn't know how to Parcel a ParseObject). So, when passing intents from TimelineFragment (aka MainActivity) to DetailActivity, I had to make a parcelable class specifically for wrapping post objects that I would use instead of the direct post class. 

## License

    Copyright 2021 Flaurencya Ciputra

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
