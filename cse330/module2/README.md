# CSE330
Haiyu Wang, 475533

Bingxin Liu, 475532

## Assignment Summary
1. Files: 
* login.php: login page, user can type into the username and submit, 
* userMainPage.php: check if the user exists and show user's files, 
* view.php: show file's contents after clicking the links, 
* uploader.php: handle the uploading actions and show the uploading status,
* downloader.php: handle the downloading actions,
* remover.php: handle the download actions,
* renamer.php: handle the renaming actions,
* upload_success.html: show success info,
* upload_fail.html: show failure info.

2. Link
* [Link]: http://ec2-13-58-248-18.us-east-2.compute.amazonaws.com/~lbxmodule2-group-475533-475532/login.php
* Users: link, mifa, zelda

3. Functions
* Login: type into usernames and log into the userMainpage
* Logout: press logout buttons and head back to the login page
* showFileList: list the files in the userMainPage
* showFileContent: show the file's contents
* mkdir: make new direcotry
* upload: upload the chosen file
* download: download the chosen file
* remove: remove the chosen file (please choose a file and then remove)
* rename: rename the chosen file


## Creative Portion
1. Download
* Users can use the radio buttons near the filenames to choose files and click
* the download button to download the selected file.

2. Rename
* Users can use the radio buttons near the filenames to choose files and click the 
* rename buttons. A new page will show and users can type into the new filename.
* After submitting, it will head back to the useMainPage.php

3. Make Directory
* There is a form to make new directories and users can navigate into those directories
* All functions (except login) behave well in the directory.

