package com.w174rd.syncuinotifactivity.utils

object Attributes {

    object progressReceiver {
        const val UPDATE_PROGRESS = "UPDATE_PROGRESS"
        const val EXTRA_PROGRESS = "EXTRA_PROGRESS"
        const val ACTION_PLUS = "ACTION_PLUS"
        const val ACTION_MINUS = "ACTION_MINUS"
    }

    object pushNotif {
        const val channelForegroundService = "channel_foreground_service"
    }

    object pref {
        const val progressPrefs = "progress_prefs"

        object key {
            const val progressKeyPref = "progressKeyPref"
        }
    }

}