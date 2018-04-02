package com.dubedivine.samples.data

import com.dubedivine.samples.data.model.*
import com.dubedivine.samples.data.remote.MvpStarterService
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


// singleton that manages data it just makes sense!! really it should because do u want duplicates of the same data??!!
@Singleton
class DataManager @Inject
constructor(private val mMvpStarterService: MvpStarterService) {

    val questions: ArrayList<Question> = arrayListOf()

    fun getPokemonList(limit: Int): Single<List<String>> {
        return mMvpStarterService.getPokemonList(limit)
                .toObservable()
                .flatMapIterable { (results) -> results } // returns an arbitary number of outputs but then its flat!
                .map { (name) -> name }
                .toList()
    }

    fun getPokemon(name: String): Single<Pokemon> {
        return mMvpStarterService.getPokemon(name)
    }

    //this function will change bro so for now we just basically search ):
    // should just get the question name
    fun getSuggestions(charSequence: CharSequence): Single<List<Question>> {
        // will change latter so for now to String will do
        return searchForQuestionUsingName(charSequence.toString(), 0)
    }

    //page is for pagination!!
    @Deprecated("bad side effects")
    fun searchForQuestionUsingName(questionName: String, page: Int): Single<List<Question>> {
        val returnQuestions = mMvpStarterService.getSearchSuggestions(questionName)
                .toObservable()
                .flatMapIterable { it }
                .map({ title -> title })
                .toList()

        returnQuestions.subscribeOn(Schedulers.computation()).subscribe(
                { questions.addAll(it) },
                { Timber.e("failed to add data to the questions array") })
        return returnQuestions
    }

    //todo : should return a boolean
    fun addVote(qId: String, userId: String, vote: Boolean): Single<StatusResponse<Boolean>> {
        return mMvpStarterService.addVoteToQuestion(qId, userId,  vote)
    }

    //todo: should return a boolean
    fun addVoteToAnswer(questionId: String, userId: String, id: String, vote: Boolean): Single<StatusResponse<Boolean>> {
        return mMvpStarterService.addVoteToAnswer(questionId, id, userId,  vote)
    }

    fun getMoreAnswers(questionId: String, page: Int): Single<List<Answer>> {
        return mMvpStarterService.getMoreAnswers(questionId, page)
                .toObservable()
                .flatMapIterable { it }
                .map({ title -> title })
                .toList()
    }

    fun getTagSuggestion(tag: CharSequence): Single<List<Tag>> {
        return mMvpStarterService
                .getTagSuggestion(tag)
                .toObservable()
                .flatMapIterable { it }
                .map { t -> t }
                .toList()
    }

    fun postQuestion(question: Question): Single<StatusResponse<Question>> {
        return mMvpStarterService.postQuestion(question)
    }

    fun postQuestionFiles(questionId: String, retrofitFileParts: List<MultipartBody.Part>): Single<StatusResponse<Question>> {
        return mMvpStarterService.postQuestionFiles(questionId, retrofitFileParts)
    }

    fun postAnswer(questionId: String, answer: Answer): Single<StatusResponse<Answer>> {
        return mMvpStarterService.postAnswer(questionId, answer)
    }

    fun postAnswerFiles(questionId: String,
                        answerId: String,
                        retrofitFileParts: List<MultipartBody.Part>): Single<StatusResponse<Answer>> {
        return mMvpStarterService.postAnswerFiles(questionId, answerId, retrofitFileParts)
    }

    fun postCommentQuestion(questionId: String, body: Comment): Single<StatusResponse<Comment>>{
        return mMvpStarterService.postCommentQuestion(questionId, body)
    }

    fun postCommentForAnswer(questionId: String, answerId: String, body: Comment): Single<StatusResponse<Comment>> {
        return mMvpStarterService.postCommentForAnswer(questionId, answerId, body)
    }

    fun signInUserWithServer(user: User): Single<StatusResponse<User>> {
        return mMvpStarterService.postSignInUserWithServer(user)
    }

    fun getQuestion(questionId: String): Single<StatusResponse<Question>> {
        return mMvpStarterService.getQuestion(questionId)
    }

    fun getVideo(videoLocation: String): Single<ResponseBody> {
        return mMvpStarterService.getVideo(videoLocation)
    }
}