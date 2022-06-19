# Comments

## Preview

Quick Video, how it could look.

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

## Integration

### Jekyll

Add this to your post layout in `_layouts/post.html`

```html
<div id="comments-section"></div>
```

Then put the necessary jquery-*.*.*.min.js and comments.js in `assets/js`.
Also add it the jquery somewhere in your html header:

```html
<script type="text/javascript" src="{{ site.baseurl }}/assets/js/jquery-3.4.1.min.js" ></script>
```

The comments js can then be added in whichever post you want to have the comments activated. It depends a little on your
Jekyll theme, how you would to that... In the end it should load at the very end of your post, so that the DOM is
rendered already. Or you wrap the whole comments.js in `$(document).ready(function)`