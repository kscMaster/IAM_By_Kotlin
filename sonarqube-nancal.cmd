# 社区版
mvn clean  -Dmaven.test.skip=true verify sonar:sonar   -Dsonar.projectKey=nancal-api   -Dsonar.host.url=http://192.168.5.213:31008   -Dsonar.login=501a5489861f9d69ec9ba71d0c1ed9c8d9091b40

# 开发版
mvn clean  -Dmaven.test.skip=true verify sonar:sonar   -Dsonar.projectKey=nancal-api   -Dsonar.host.url=http://192.168.5.213:31007   -Dsonar.login=64dfd530dc31afef7a652ca31eb75811aec1fc31