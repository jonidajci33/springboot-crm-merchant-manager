# Supabase Storage Setup Guide

This guide will help you configure Supabase Storage for file uploads in your Spring Boot application.

## Prerequisites

1. A Supabase project (create one at [supabase.com](https://supabase.com))
2. Your Supabase project URL
3. Your Supabase service role key

## Step 1: Create a Storage Bucket

1. Go to your Supabase Dashboard
2. Navigate to **Storage** in the left sidebar
3. Click **New bucket**
4. Enter a bucket name (e.g., `merchant-files`)
5. Choose whether the bucket should be **Public** or **Private**:
   - **Public**: Files are accessible via public URL
   - **Private**: Files require authentication/signed URLs

## Step 2: Configure Bucket Policies (Optional)

For private buckets, you may want to set up Row Level Security (RLS) policies:

```sql
-- Allow authenticated users to upload files
CREATE POLICY "Allow authenticated uploads"
ON storage.objects FOR INSERT
TO authenticated
WITH CHECK (bucket_id = 'merchant-files');

-- Allow users to read their own files
CREATE POLICY "Allow users to read own files"
ON storage.objects FOR SELECT
TO authenticated
USING (auth.uid()::text = (storage.foldername(name))[1]);

-- Allow users to delete their own files
CREATE POLICY "Allow users to delete own files"
ON storage.objects FOR DELETE
TO authenticated
USING (auth.uid()::text = (storage.foldername(name))[1]);
```

## Step 3: Get Your Credentials

1. Go to **Settings** > **API** in your Supabase Dashboard
2. Copy the following:
   - **Project URL**: `https://your-project-id.supabase.co`
   - **Service Role Key**: Your `service_role` key (keep this secret!)

## Step 4: Configure Application Properties

Add the following to your `application.properties` file:

```properties
# Supabase Storage Configuration
cloud.storage.provider=supabase
cloud.storage.bucket-name=merchant-files
cloud.storage.endpoint=https://your-project-id.supabase.co
cloud.storage.secret-key=your-service-role-key-here
cloud.storage.max-file-size=10485760

# Multipart file size limits
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB
```

## Step 5: Test File Upload

You can test file upload using curl:

```bash
curl -X POST http://localhost:8080/api/files/upload \
  -H "Authorization: Bearer your-jwt-token" \
  -F "file=@/path/to/your/file.pdf" \
  -F "entityType=merchant" \
  -F "entityId=1" \
  -F "isPublic=true"
```

## API Endpoints

### Upload Single File
```
POST /api/files/upload
Content-Type: multipart/form-data

Parameters:
- file: The file to upload (required)
- entityType: Entity type (optional, e.g., "merchant", "user")
- entityId: Entity ID (optional)
- isPublic: Boolean for public access (optional, default: false)
```

### Upload Multiple Files
```
POST /api/files/upload-multiple
Content-Type: multipart/form-data

Parameters:
- files: Array of files to upload (required)
- entityType: Entity type (optional)
- entityId: Entity ID (optional)
- isPublic: Boolean for public access (optional, default: false)
```

### Download File
```
GET /api/files/download/{fileId}
```

### Get File Metadata
```
GET /api/files/metadata/{fileId}
```

### Get File URL
```
GET /api/files/url/{fileId}
```

### Get Files by User
```
GET /api/files/user/{userId}
```

### Get Files by Entity
```
GET /api/files/entity/{entityType}/{entityId}
```

### Delete File
```
DELETE /api/files/{fileId}
```

## File URL Structure

### Public Files
Files in public buckets are accessible via:
```
https://your-project-id.supabase.co/storage/v1/object/public/{bucket}/{filename}
```

### Private Files
Private files require authentication. The API will return a download URL that requires a valid JWT token.

## Security Best Practices

1. **Never commit your service role key** - Use environment variables:
   ```properties
   cloud.storage.secret-key=${SUPABASE_SERVICE_ROLE_KEY}
   ```

2. **Use private buckets for sensitive data** - Only expose what needs to be public

3. **Implement file type validation** - Validate file types before upload

4. **Set appropriate file size limits** - Prevent large file uploads

5. **Use signed URLs for temporary access** - For private files that need temporary public access

## Advanced Features

### Signed URLs (For Private Files)

The `SupabaseStorageService` includes a method to create signed URLs:

```java
String signedUrl = supabaseStorageService.createSignedUrl(filename, 3600); // 1 hour
```

### File Organization

You can organize files by entity type and ID by using the `entityType` and `entityId` parameters during upload. This helps with:
- Querying files by merchant, user, etc.
- Organizing file cleanup
- Managing file permissions

## Troubleshooting

### Upload fails with 401 Unauthorized
- Check that your service role key is correct
- Verify the key is set in application.properties

### Upload fails with 403 Forbidden
- Check bucket policies in Supabase Dashboard
- Ensure the bucket exists

### File not accessible
- For private buckets, verify RLS policies
- For public buckets, check the bucket is set to public in Supabase

### Large files fail to upload
- Increase `spring.servlet.multipart.max-file-size` and `max-request-size`
- Increase `cloud.storage.max-file-size`
- Check your Supabase plan limits

## Support

For more information, visit:
- [Supabase Storage Documentation](https://supabase.com/docs/guides/storage)
- [Supabase Storage API Reference](https://supabase.com/docs/reference/javascript/storage)
