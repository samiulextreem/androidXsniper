# Android X Sniper Configuration Example

This file shows example configuration values. 
**DO NOT commit actual API credentials to version control!**

## Required Configuration

Configure these settings in the app:

### X API Credentials
- **Bearer Token**: Your X API v2 Bearer Token
  - Get it from: https://developer.twitter.com/en/portal/dashboard
  - Example format: `AAAAAAAAAAAAAAAAAAAAAMLheAAAAAAA0%2BuSeid...`

### Target User
- **Username**: The X user to monitor
  - Example: `elonmusk`
  - Do not include the @ symbol

### Server Configuration
- **Server URL**: Your server endpoint that receives posts
  - Example: `https://api.yourserver.com/webhook/x-posts`
  - Must be HTTPS in production
  - Should return 2xx status code for success

## Server Endpoint Requirements

Your server should accept POST requests with this JSON structure:

```json
{
  "id": "1234567890123456789",
  "text": "The content of the post",
  "authorId": "44196397",
  "authorUsername": "elonmusk",
  "createdAt": "2025-10-23T15:30:00.000Z",
  "url": "https://twitter.com/elonmusk/status/1234567890123456789"
}
```

## Example Server Response

Success response (200 OK):
```json
{
  "status": "success",
  "message": "Post received",
  "postId": "1234567890123456789"
}
```

## Rate Limits

X API v2 Free Tier limits:
- 500,000 Tweets per month
- 10,000 requests per month

Essential Access:
- 1,500 Tweets per month
- Rate limit per endpoint

Elevated Access:
- 2,000,000 Tweets per month
- Higher rate limits

## Monitoring Interval

The app checks for new posts every 60 seconds by default.
This is configurable in `XSniperService.kt`:
```kotlin
private const val POLL_INTERVAL_MS = 60000L // 1 minute
```

## Testing

For testing, you can use:
- RequestBin: https://requestbin.com/
- Webhook.site: https://webhook.site/
- ngrok: https://ngrok.com/ (for local development)

## Security Notes

1. Never commit your Bearer Token to git
2. Rotate your tokens regularly
3. Use environment-specific tokens
4. Implement authentication on your server
5. Monitor API usage to avoid rate limits
6. Use HTTPS for all production endpoints

## Troubleshooting

### Invalid Token Error
- Verify your token hasn't expired
- Check for extra spaces or characters
- Regenerate token if necessary

### Posts Not Being Detected
- Verify the username is correct
- Check if the account has posted recently
- Review app logs for API errors
- Verify network connectivity

### Server Not Receiving Posts
- Check server URL is correct
- Verify server is accessible from the internet
- Check server logs for incoming requests
- Test endpoint with curl or Postman

## Support

For issues or questions:
- Open an issue on GitHub
- Check existing issues for solutions
- Review the README.md for setup instructions
