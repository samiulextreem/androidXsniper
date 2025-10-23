# Android X Sniper

An Android application that monitors Elon Musk's (or any user's) X (formerly Twitter) posts in real-time and relays them to a configured server endpoint.

## Features

- 🎯 Monitor any X (Twitter) user's posts in real-time
- 📡 Automatically relay new posts to a server endpoint
- 🔄 Background monitoring with foreground service
- 🔔 Notifications for new posts detected
- ⚙️ Configurable API credentials and server URL
- 📱 Simple and intuitive UI

## Prerequisites

Before using this app, you'll need:

1. **X (Twitter) API Access**: 
   - Sign up for X API access at [Twitter Developer Portal](https://developer.twitter.com)
   - Create a new app in the developer portal
   - Generate a Bearer Token with read permissions

2. **Server Endpoint**:
   - A server endpoint that can receive POST requests with JSON payloads
   - The endpoint should accept the following JSON structure:
   ```json
   {
     "id": "post_id",
     "text": "post content",
     "authorId": "author_id",
     "authorUsername": "username",
     "createdAt": "timestamp",
     "url": "https://twitter.com/username/status/post_id"
   }
   ```

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/samiulextreem/androidXsniper.git
   cd androidXsniper
   ```

2. Open the project in Android Studio

3. Build and run the app on your Android device or emulator

## Configuration

1. Launch the app
2. Enter the following information:
   - **Target Username**: The X username to monitor (e.g., `elonmusk`)
   - **Bearer Token**: Your X API Bearer Token
   - **Server URL**: Your server endpoint URL (e.g., `https://your-server.com/api/posts`)

3. Tap "Start Sniping" to begin monitoring

## Usage

### Starting the Monitor
1. Configure your API credentials and server URL
2. Tap "Start Sniping"
3. The app will start monitoring in the background
4. You'll see a persistent notification indicating the service is active

### Stopping the Monitor
1. Open the app
2. Tap "Stop Sniping"
3. The monitoring service will stop

### How It Works
- The app polls the X API every 60 seconds for new posts
- When a new post is detected, it's immediately relayed to your server
- The app maintains the last post ID to avoid duplicate relays
- All operations run in a background service, so monitoring continues even when the app is closed

## Permissions

The app requires the following permissions:
- `INTERNET`: To communicate with X API and your server
- `ACCESS_NETWORK_STATE`: To check network connectivity
- `FOREGROUND_SERVICE`: To run monitoring in the background
- `POST_NOTIFICATIONS`: To display notifications (Android 13+)

## API Rate Limits

Be aware of X API rate limits:
- The app polls every 60 seconds by default
- X API v2 free tier has limited requests per month
- Consider upgrading to a paid tier for production use

## Server Endpoint Example

Here's a simple Node.js Express server example to receive posts:

```javascript
const express = require('express');
const app = express();

app.use(express.json());

app.post('/api/posts', (req, res) => {
  const post = req.body;
  console.log('Received post:', post);
  
  // Process the post (save to database, trigger actions, etc.)
  
  res.json({ status: 'success', message: 'Post received' });
});

app.listen(3000, () => {
  console.log('Server running on port 3000');
});
```

## Security Considerations

- ⚠️ **Never commit your API credentials** to version control
- 🔒 Store sensitive data securely using Android's encrypted SharedPreferences in production
- 🛡️ Use HTTPS for your server endpoint
- 🔑 Consider implementing authentication for your server endpoint

## Building from Source

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug
```

## Requirements

- Android 7.0 (API 24) or higher
- Android Studio Arctic Fox or later
- Gradle 7.0 or later

## Dependencies

- AndroidX Core KTX
- Material Design Components
- OkHttp for networking
- Gson for JSON parsing
- Kotlin Coroutines for async operations
- WorkManager for background tasks

## Troubleshooting

### App not detecting new posts
- Verify your Bearer Token is correct and hasn't expired
- Check that the target username exists and is spelled correctly
- Review Android logs for error messages
- Ensure you have network connectivity

### Posts not being relayed to server
- Verify your server URL is correct and accessible
- Check that your server is running and accepting POST requests
- Review server logs for incoming requests
- Test your server endpoint with a tool like Postman

### Service stops running
- Ensure battery optimization is disabled for the app
- Check that the app has the required permissions
- Some manufacturers have aggressive battery management; add the app to the whitelist

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Disclaimer

This app is for educational and development purposes. Ensure you comply with X's Terms of Service and API usage policies. Be respectful of rate limits and user privacy.

## Support

For issues, questions, or contributions, please open an issue on GitHub.

---

Made with ❤️ for the X developer community