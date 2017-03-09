ant jar
ssh wikivents.nl cp site/wikivents-0.0.1.jar wikivents-`date "+%Y-%m-%d-%H.%M.%S"`.jar
scp wikivents-0.0.1.jar  wikivents.nl:site
ssh wikivents.nl sudo service wikivents restart
