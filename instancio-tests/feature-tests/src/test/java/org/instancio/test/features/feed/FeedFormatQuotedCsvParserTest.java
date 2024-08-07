package org.instancio.test.features.feed;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedFormatQuotedCsvParserTest {

    @Feed.Source(string = """
        col1, col2, col3
        "a,b", c, "d, e,  f"
        """)
    interface SampleFeed extends Feed {
        FeedSpec<String> col1();
        FeedSpec<String> col2();
        FeedSpec<String> col3();
    }

    @Test
    void sampleFeed(){
        FeedFormatQuotedCsvParserTest.SampleFeed feed = Instancio.createFeed(FeedFormatQuotedCsvParserTest.SampleFeed.class);
        assertThat(feed.col1().get()).isEqualTo("a,b");
        assertThat(feed.col2().get()).isEqualTo("c");
        assertThat(feed.col3().get()).isEqualTo("d, e,  f");
    }
}
