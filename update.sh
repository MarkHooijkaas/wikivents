ant jar 
ssh wikivents cp site/wikivents-0.0.1.jar wikivents-`date "+%Y-%m-%d-%H.%M.%S"`.jar
scp wikivents-0.0.1.jar  wikivents:site
ssh wikivents sudo service wikivents restart