package repackaged.com.mangofactory.swagger.ordering;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.mangofactory.swagger.models.dto.ApiListingReference;

/**
 * Orders ApiListingReference's by their position
 */
public class ResourceListingPositionalOrdering extends Ordering<ApiListingReference> {
  @Override
  public int compare(ApiListingReference first, ApiListingReference second) {
    return Ints.compare(first.getPosition(), second.getPosition());
  }
}
