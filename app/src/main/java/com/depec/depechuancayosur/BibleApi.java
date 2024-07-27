package com.depec.depechuancayosur;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BibleApi {
    @GET("verses/{version}/{book}/{chapter}:{verse}")
    Call<BibleVerse> getVerse(
            @Path("version") String version,
            @Path("book") String book,
            @Path("chapter") int chapter,
            @Path("verse") int verse
    );
}
