package org.dd4t.contentmodel;

import org.joda.time.DateTime;

/**
 * Interface for the component template
 *
 * @author bjornl
 */
public interface ComponentTemplate extends Item, HasMetadata {
    public DateTime getRevisionDate();

    public void setRevisionDate(DateTime date);
}
