package jp.mydns.kokoichi0206.gmail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import jp.mydns.kokoichi0206.gmail.R
import jp.mydns.kokoichi0206.gmail.accountList
import jp.mydns.kokoichi0206.gmail.model.Account

@Composable
fun AccountsDialog(openDialog: MutableState<Boolean>) {
    Dialog(
        onDismissRequest = {
            openDialog.value = false
        },
        properties = DialogProperties(
            dismissOnClickOutside = false,
        )
    ) {
        AccountsDialogUI(openDialog = openDialog)
    }
}

@Composable
fun AccountsDialogUI(
    modifier: Modifier = Modifier,
    openDialog: MutableState<Boolean>,
) {
    Card {
        Column(
            modifier = modifier
                .background(Color.White)
                .padding(bottom = 16.dp),
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = {
                    // Close button
                    openDialog.value = false
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                    )
                }
                Image(
                    modifier = modifier
                        .size(30.dp)
                        .weight(2.0f),
                    painter = painterResource(id = R.drawable.bilibili),
                    contentDescription = "",
                )
            }

            AccountItem(account = accountList[0])

            Row(
                modifier = modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = modifier
                        .requiredWidth(150.dp),
                    shape = RoundedCornerShape(50.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color.Gray
                    )
                ) {
                    Text(
                        text = "Google Account",
                        modifier = modifier
                            .padding(8.dp),
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(modifier = modifier.width(10.dp))
            }

            Divider(modifier = modifier.padding(top = 16.dp))

            // Iterate for accounts
            accountList.drop(1)
                .forEach {
                    AccountItem(account = it)
                }

            AddAccount(icon = Icons.Outlined.PersonAddAlt, title = "Add Another Account")
            AddAccount(icon = Icons.Outlined.ManageAccounts, title = "Manage Accounts on this device")

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(text = "Privacy Policy")
                Card(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .size(5.dp),
                    shape = CircleShape,
                    backgroundColor = Color.Black,
                ) {

                }
                Text(text = "Terms of Service")
            }
        }
    }
}

@Composable
fun AccountItem(account: Account) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (account.icon != null) {
            Image(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color = Color.Gray),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Profile",
            )
        } else {
            Text(
                text = account.userName[0].toString(),
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color = Color.Gray),
                textAlign = TextAlign.Center,
            )
        }
        Column(
            modifier = Modifier
                .weight(2.0f)
                .padding(start = 16.dp, end = 16.dp),
        ) {
            Text(
                text = account.userName,
                fontWeight = FontWeight.SemiBold,
            )
            Text(text = "doe.1031.@gmail.com")
        }
        Text(
            modifier = Modifier
                .padding(end = 16.dp),
            text = when {
                account.unReadMails < 99 -> "${account.unReadMails}"
                else -> "99+"
            }
        )
    }
}

@Composable
fun AddAccount(
    icon: ImageVector,
    title: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
    ) {
        IconButton(onClick = {}) {
            Icon(
                imageVector = icon,
                contentDescription = "",
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 8.dp, start = 8.dp),
        )
    }
}
