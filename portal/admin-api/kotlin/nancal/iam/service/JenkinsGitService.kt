//package nancal.iam.service
//
//import nbcp.comm.*
//import nbcp.utils.CodeUtil
//import org.apache.commons.io.FileUtils
//import org.eclipse.jgit.api.CreateBranchCommand
//import org.eclipse.jgit.api.Git
//import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.stereotype.Service
//import java.io.File
//import java.io.FileOutputStream
//import java.util.concurrent.TimeUnit
//import java.util.concurrent.locks.ReentrantLock
//import kotlin.concurrent.withLock
//
//
//@Service
//class JenkinsGitService {
//    @Value("\${app.jenkins.git-url:}")
//    var gitUrl = "";
//
//    @Value("\${app.jenkins.git-path:}")
//    var gitPath = ""
//
//    @Value("\${app.jenkins.user-name:ops-jenkins}")
//    var jenkinsUserName = "";
//
//    @Value("\${app.jenkins.password:Nancal-123}")
//    var jenkinsPassword = "";
//
//    companion object {
////        private val lock_object = ReentrantLock()
//    }
//
//    fun addFile(branchName: String, jenkinsPath: String, content: String, userName: String): ApiResult<String> {
////        if (lock_object.tryLock(1, TimeUnit.SECONDS) == false) {
////            return ApiResult.error("有任务正在执行，请稍后再试！")
////        }
//        if (gitUrl.isEmpty()) {
//            gitUrl = "http://saas-test.nancal.com:31004/ops/jenkins.git"
//        }
//        if (gitPath.isEmpty()) {
//            gitPath = "/opt/jenkins"
//        }
//
//        var gitDir = File(gitPath + "/" + CodeUtil.getCode())
//
//        try {
//            gitDir.mkdirs()
//
//            val provider = UsernamePasswordCredentialsProvider(jenkinsUserName, jenkinsPassword)
//            Git
//                .cloneRepository()
//                .setURI(gitUrl)
//                .setDirectory(gitDir)
//                .setCredentialsProvider(provider)
//                .call()
//                .use { git ->
//                    if (branchName.HasValue && branchName != "master") {
//                        git
//                            .checkout()
//                            .setCreateBranch(true)
//                            .setName(branchName)
//                            .setStartPoint("origin/${branchName}")
//                            .call()
//                    }
//
//
//                    var jenkinsFile = gitDir.FullName + "/" + jenkinsPath;
//                    FileOutputStream(jenkinsFile, false).use {
//                        it.write(content.toByteArray(const.utf8))
//                        it.flush()
//                    }
//
//                    git
//                        .add()
//                        .addFilepattern(".")
//                        .call()
//
//                    git.commit().setMessage("${jenkinsPath} by ${userName}").call()
//
//                    git.push().setCredentialsProvider(provider).call()
//                }
//        } finally {
////            lock_object.unlock()
//            gitDir.deleteRecursively()
//        }
//
//        return ApiResult()
//    }
//}