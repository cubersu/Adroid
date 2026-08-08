package com.adroid.cryptosignal.data.remote.websocket

import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

/**
 * BtcTurk Pro websocket (wss://ws-feed-pro.btcturk.com) client-to-server envelope.
 *
 * The subscribe/unsubscribe envelope shape — `[151, {"type":151,"channel":...,"event":...,
 * "join":true|false}]` — is confirmed by BtcTurk's public websocket-feed docs. The exact
 * numeric *response* type codes the server uses per channel (e.g. for ticker vs. tradeview
 * pushes) could not be verified from this build environment (docs.btcturk.com is not
 * reachable here), so [BtcTurkWebSocketClient] identifies incoming messages by payload shape
 * rather than by hard-coding a response type constant. Verify against the current docs
 * (https://docs.btcturk.com/websocket-feed/models) and tighten the dispatch in
 * [BtcTurkWebSocketClient.onMessage] if the shape-based heuristic ever misclassifies a message.
 */
object BtcTurkSocketProtocol {
    const val TYPE_SUBSCRIBE = 151

    const val CHANNEL_TICKER = "ticker"
    const val CHANNEL_TRADEVIEW = "tradeview"

    const val TRADEVIEW_RESOLUTION_1_MIN = "1"

    fun tradeViewEvent(pairSymbol: String, resolution: String = TRADEVIEW_RESOLUTION_1_MIN): String =
        "$pairSymbol\\$resolution"

    fun subscribeMessage(channel: String, event: String, join: Boolean): String {
        val envelope = buildJsonArray {
            add(JsonPrimitive(TYPE_SUBSCRIBE))
            add(
                buildJsonObject {
                    put("type", JsonPrimitive(TYPE_SUBSCRIBE))
                    put("channel", JsonPrimitive(channel))
                    put("event", JsonPrimitive(event))
                    put("join", JsonPrimitive(join))
                }
            )
        }
        return envelope.toString()
    }
}
