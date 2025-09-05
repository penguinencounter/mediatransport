package penguinencounter.mediatransport.networking.figura

object MediaTransportFigura {
    val channelS2C = "transport_received".hashCode()
    @Suppress("RedundantSuppression") /* so the actual one is the "make const" suggestion but that disappears if it's annotated by anything */
    val channelC2S = "transport_send"
}