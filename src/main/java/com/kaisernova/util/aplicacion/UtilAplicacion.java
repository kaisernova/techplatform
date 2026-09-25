package com.kaisernova.util.aplicacion;

import java.security.SecureRandom;

public final class UtilAplicacion {
	private UtilAplicacion() {
		super();
	}
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(ALPHANUMERIC.length());
            sb.append(ALPHANUMERIC.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Given a YouTube URL, attempt to extract the video ID and return the corresponding embed URL.
     * Supports YouTube Shorts as well.
     * @param urlVideo
     * @return
     */
    public static String obtenerUrlYoutubeEmbed(String urlVideo) {
		
		try {
			String videoId = obtenerVideoIdDeUrlYoutube(urlVideo);
			// Validate videoId roughly: YouTube IDs are 11 chars of [A-Za-z0-9-_], but sometimes playlists or other formats
			if (videoId != null) {
				videoId = videoId.trim();
				// Remove common prefixes/suffixes
				int q = videoId.indexOf('?');
				if (q > -1) videoId = videoId.substring(0, q);
				int amp = videoId.indexOf('&');
				if (amp > -1) videoId = videoId.substring(0, amp);
				int slash = videoId.indexOf('/');
				if (slash > -1) videoId = videoId.substring(0, slash);
				if (!videoId.isEmpty()) {
					return "https://www.youtube.com/embed/" + videoId;
				}
			}
		} catch (Exception e) {
			// In case of unexpected parsing issues, fall through and return original value
		}
		// Not recognized as a known youtube URL - return original trimmed value
		return urlVideo != null ? urlVideo.trim() : null;
    }
    
    public static String obtenerVideoIdDeUrlYoutube(String urlVideo) {
    	if (urlVideo == null)
			return null;
		String trimmed = urlVideo.trim();
		if (trimmed.isEmpty())
			return trimmed;
		// Try to extract YouTube video id from common URL patterns
		// Patterns handled:
		// - https://www.youtube.com/watch?v=VIDEOID
		// - https://youtube.com/watch?v=VIDEOID&...
		// - https://youtu.be/VIDEOID
		// - https://www.youtube.com/embed/VIDEOID
		// - https://www.youtube.com/v/VIDEOID
		// - https://www.youtube.com/shorts/VIDEOID
		// We will parse strings heuristically without external libs.
		String lower = trimmed.toLowerCase();
		String videoId = null;
    	if (lower.contains("youtube.com/watch")) {
			int idx = trimmed.indexOf("v=");
			if (idx >= 0) {
				int start = idx + 2;
				int amp = trimmed.indexOf('&', start);
				int hash = trimmed.indexOf('#', start);
				int end = amp > -1 ? amp : (hash > -1 ? hash : trimmed.length());
				videoId = trimmed.substring(start, end);
			}
		} else if (lower.contains("youtu.be/")) {
			int idx = lower.indexOf("youtu.be/");
			int start = idx + "youtu.be/".length();
			int end = start;
			while (end < trimmed.length()) {
				char c = trimmed.charAt(end);
				if (c == '?' || c == '&' || c == '/' || c == '#') break;
				end++;
			}
			videoId = trimmed.substring(start, end);
		} else if (lower.contains("/embed/")) {
			int idx = lower.indexOf("/embed/");
			int start = idx + "/embed/".length();
			int end = start;
			while (end < trimmed.length()) {
				char c = trimmed.charAt(end);
				if (c == '?' || c == '&' || c == '/' || c == '#') break;
				end++;
			}
			videoId = trimmed.substring(start, end);
		} else if (lower.contains("/v/")) {
			int idx = lower.indexOf("/v/");
			int start = idx + "/v/".length();
			int end = start;
			while (end < trimmed.length()) {
				char c = trimmed.charAt(end);
				if (c == '?' || c == '&' || c == '/' || c == '#') break;
				end++;
			}
			videoId = trimmed.substring(start, end);
		} else if (lower.contains("/shorts/")) {
			// Support for YouTube Shorts URLs like https://www.youtube.com/shorts/VIDEOID
			int idx = lower.indexOf("/shorts/");
			int start = idx + "/shorts/".length();
			int end = start;
			while (end < trimmed.length()) {
				char c = trimmed.charAt(end);
				if (c == '?' || c == '&' || c == '/' || c == '#') break;
				end++;
			}
			videoId = trimmed.substring(start, end);
		}
    	return videoId != null ? videoId.trim() : null;
    }
 
}