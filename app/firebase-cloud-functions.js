const { onValueCreated } = require('firebase-functions/v2/database')
const admin = require('firebase-admin')

admin.initializeApp();

expors.onUserItemAdded = onValueCreated('/users/{userId}/messageInbox/{messageId}', async (event) => {
    const newItem = event.data.val();
    const userId = event.params.userId;
    
    const tokenSnap = await admin.database().ref(`/users/${userId}/notificationToken`)
    const fcmToken = tokenSnap.val();

    if(!fcmToken) {
        console.log(`No FCM token for user ${userId}`);
        return;
    }

    const message = {
        token: fcmToken,
        notification: {
            title: `You have a new message from ${newItem.sender}!`,
            body: `${newItem.messageString}`
        },
    }

    try {
        const response = await admin.messaging().send(message);
        console.log("Successfully sent FCM message: ", response);
        return { success: true, messageId: response };
    } catch (error) {
        console.error("Error routing FCM notification request: ", error);
        throw error;
    }
})