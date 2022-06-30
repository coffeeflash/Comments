# Comments

This app will provide an API and an exemplary integration to provide a comments section to a simple static website. I developed it
for two jekyll blogs, but it is easily integrated in any blog.

The cool thing about this app is, that users do not need to register to any services. They can just post comments. For
security reasons I implemented some protection in form of hash quizes (similar to the proof of work concept with bitcoin)
and simple ip blocking on certain condtions.

## Preview

Quick Video, how it could look.

<img src="./integration/jekyll/comments-preview.GIF" width="100%" height="100%"/>

## Quickstart

### Backend
For a quick installtion just start with the provided docker-compose file. Be sure to change the credentials.
After successful start you will have following services:

* The main comments app, runs on port 8071 (default value in docker-compose). The comments api is mapped on `/api`
* A mongo-db ist started and its storage, you will find in a docker-volume called mongo-data. I recommend to backup
this volume regularly.
* A mongo-express container, which provides a nice web-interface on port 80802 (default value in docker-compose). At
the moment, I only provide that as an admin interface. You can easily browse your comments there, and delete unwanted
stuff... There is also the possibility to export everything or single documents.

### Frontend

You can integrate this comments app in whatever frontend you want. Just stick to the api documentation or ask me.
I use it for 2 jekyll blogs, That's why I provided that javascript files as an example. I would gladly add more
integration scripts. Don't hesitate to contribute...

I implemented a very strict input validation in the backend. Users are not allowed to add code samples and similar
things for security reasons. I recommend to take the same regex for frontend validation
(see `/integration/jekyll/comments.js`).

Further instructions for integration you will find in the more detailed Integration Chapter.

## Configuration

Following environment variables can be set to fit your needs the best as possible:

```yaml
environment:
  IP_BLOCK_TIME: 900
  QUIZ_COUNT: 5
  QUIZ_VALIDITY_SECONDS: 120
  QUIZ_COMPLEXITY: 2
  ADMIN_NAME: the_bloggers_name
  SPRING_DATA_MONGODB_HOST: mongo
  SPRING_DATA_MONGODB_DATABASE: comments
  SPRING_DATA_MONGODB_AUTHENTICATION_DATABASE: admin
  SPRING_DATA_MONGODB_USERNAME: root
  SPRING_DATA_MONGODB_PASSWORD: pleaseChangeMe
  SPRING_SECURITY_USER_NAME: user
  SPRING_SECURITY_USER_PASSWORD: PleaseChangeMe
```

The `QUIZ_COMPLEXITY` is the number of zero bytes needed to solve the quiz. I strongly recommend to leave it 2. 3 takes
much longer in this single threaded client scenario. If you want to make it a bit harder, just increase the `QUIZ_COUNT`.
If you increase `QUIZ_COMPLEXITY` or/and `QUIZ_COUNT`, you should also test if the time suffices on your target client
devices...

`PRING_SECURITY_USER_NAME` and `SPRING_SECURITY_USER_PASSWORD` are for the http basic authentication,
whereas `ADMIN_NAME` is just the blogger's name, which can be shown with the reply text.
## Integration

### Jekyll

Add this to your post layout in `_layouts/post.html`

```html
<div id="comment-section"></div>
```

Then put the necessary jquery-*.*.*.min.js and comments.js in `assets/js`.
Also add it the jquery somewhere in your html header (`_includes/head.html):

```html
<script type="text/javascript" src="{{ site.baseurl }}/assets/js/jquery-3.4.1.min.js" ></script>
```

The comments js can then be added in whichever post you want to have the comments activated. It depends a little on your
Jekyll theme, how you would to that... In the end it should load at the very end of your post, so that the DOM is
rendered already. Or you wrap the whole comments.js in `$(document).ready(function)`

## Development

In general it is a simple spring boot app. If you are familiar with spring or spring boot you should be fine. I did not
document a lot yet, but [here](https://tobisyurt.net/comments-app) I summarized some things and explained the basic
functionalities.

I will expand this section, when a contributor needs it...
