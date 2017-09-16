package es.guillermoorellana.amirichyet.logic.marketdata

import es.guillermoorellana.amirichyet.core.UnwrapOptionalTransformer
import es.guillermoorellana.amirichyet.core.domain.interactor.ReactiveInteractor.RetrieveInteractor
import es.guillermoorellana.amirichyet.service.marketdata.data.MarketData
import es.guillermoorellana.amirichyet.service.marketdata.data.MarketDataRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import java.util.*

class RetrieveMarketData(
        private val marketDataRepository: MarketDataRepository
) : RetrieveInteractor<Void, List<MarketData>> {

    override fun getStream(params: Optional<Void>): Flowable<List<MarketData>> {
        return marketDataRepository.getAllMarketData()
                .flatMapSingle(this::fetchWhenNoneAndThenDrafts)
                .compose(UnwrapOptionalTransformer.create())
    }

    private fun fetchWhenNoneAndThenDrafts(drafts: Optional<List<MarketData>>): Single<Optional<List<MarketData>>> =
            fetchWhenNone(drafts).andThen(Single.just(drafts))

    private fun fetchWhenNone(drafts: Optional<List<MarketData>>): Completable =
            if (drafts.isPresent) Completable.complete() else marketDataRepository.fetchMarketData()
}
