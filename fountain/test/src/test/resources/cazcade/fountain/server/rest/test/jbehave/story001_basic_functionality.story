Scenario: A user is created and basic object creation is checked.

Given the servers are running
Given a user called test_user
When we wait 2 seconds
Then the user can retrieve an object called TestObject4 from a pool called %home% of type Image.Bitmap.2DBitmap
Then the user can retrieve an RSS feed called rssdfeed01 from their stream pool pointing to http://newsrss.bbc.co.uk/rss/newsonline_uk_edition/front_page/rss.xml

Scenario: An object is updated

Given a user called test_user
Then the user can retrieve an object called TestObject4 from a pool called %home% of type Image.Bitmap.2DBitmap
When the user updates the object's rights with "(C) Cazcade Ltd. We'd like to thank the special characters & < > '"
Then the object's rights are "(C) Cazcade Ltd. We'd like to thank the special characters & < > '"
When the user adds a comment to the object saying "Hello World"
Then the object has 1 comment

Scenario: A link to an object is created.

Given a user called test_user
Then the user can retrieve an object called TestObject4 from a pool called %home% of type Image.Bitmap.2DBitmap
When the user links to the object from the pool called %home%/public
Then the user can retrieve an object called TestObject4 from a pool called %home%/public of type Image.Bitmap.2DBitmap

Scenario: An object is relocated

Given a user called test_user2
Then the user can retrieve an object called TestObject4 from a pool called %home% of type Image.Bitmap.2DBitmap
When the user relocates the object to the pool called %home%/public
Then there is no object called TestObject4 in the pool called %home%
And the user can retrieve an object called TestObject4 from a pool called %home%/public of type Image.Bitmap.2DBitmap

Scenario: A pool is created and an object is created in it

Given a user called test_user2
When the user creates a pool called nicePool with a title of "A Nice Pool" in the pool called %home%
And we wait 2 seconds
Then a pool called %home%/nicePool exists with a title of "A Nice Pool"
When the user creates objects of type Image.Bitmap.2DBitmap in pool %home%/nicePool as
|name|view.view2d.width|view.view2d.height|view.view2d.x|view.view2d.y|image.url|
|NiceObject1|100|100|200|800|http://wwwimg.bbc.co.uk/feedengine/homepage/images/news/_47595354_womankitchen_146x110.gif|
Then the user can retrieve an object called NiceObject1 from a pool called %home%/nicePool of type Image.Bitmap.2DBitmap
