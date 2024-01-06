package uz.gita.musicplayer_pro.data.model

enum class CommandEnum(val amount: Int) {
    PREV(1), NEXT(2), PLAY(3), PAUSE(4), CLOSE(5), MANAGE(6), CONTINUE(7), UPDATE_SEEKBAR(8)
}