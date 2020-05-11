package com.ordy.app.ui.groups.overview.changeName

import com.ordy.app.ui.groups.overview.OverviewGroupActivity
import com.ordy.app.ui.groups.overview.OverviewGroupViewModel
import java.io.Serializable

class ChangeGroupNameDialogArgumentsWrapper(
    val viewModel: OverviewGroupViewModel,
    val activity: OverviewGroupActivity
): Serializable