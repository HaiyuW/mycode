# CSE330
Bingxin Liu, 475532

Haiyu Wang, 475533

## Assignment Summary
1.Files
 * PHP files:
    * database.php - set up the database
    * login.php, logout.php, signup.php - handle login/logout/signup event
    * showEvent.php, handleEvent.php - show lists of events and handle add/delete/modify events
    * showTag.php, handleTag.php - show lists of tags and handle add/delete tags
    * showUsers.php, handleGroupEvent.php - show lists of group events and users to form groups
    * handleContact.php, handleShareEvent.php - implement share function
    
 * Scripts:
    * calendar.js - display calendar and month swithes
    * function.js - useful functions to deal with modal
    * handleCalendarEvents.js - handle calendar events
    * handleContactsEvents.js - handle contacts events
    * handleLoad.js - handle functions after loading

 * Styles:
    * calendar.css - change view of calendar

 * calendar.html - html page for the whole calendar
 * createTable - commands to create Table.

2.Link
* http://ec2-13-58-248-18.us-east-2.compute.amazonaws.com/~lbx/module5-group-475532-475533/calendar.html
 
3.Creative Portion
 * Users can tag an event with a particular category and enable/disable those tags in the calendar view
    * Users can add a tag when creating events and can add tags by clicking "+" button.
    * Users can delete tags on clicking the tags
    
 * Users can share their calendar with additional users.
    * Users can type into the email and add the users to Contacts
    * After clicking the body of the events, it will "Share" button
    * After clicking the "Share" button, users can choose the users in their contacts and share the selected event.
    * Users can view the shared events from other users.
 
 * Users can create group events that display on multiple users calendars
    * In adding new events modal, a list of users will show when the "Is it group?" checkbox is checked.
    * Users can selected other users to form a group and this new event will be a group event.
    * All group members can see the group events and can delete them.
 
 
    

