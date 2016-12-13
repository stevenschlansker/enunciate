package com.webcohesion.enunciate.modules.jaxrs.api.impl;

import com.webcohesion.enunciate.api.datatype.DataTypeReference;
import com.webcohesion.enunciate.api.resources.MediaTypeDescriptor;
import com.webcohesion.enunciate.modules.jaxrs.model.util.MediaType;

import java.util.Map;

/**
 * @author Ryan Heaton
 */
public class MediaTypeDescriptorImpl implements MediaTypeDescriptor {

  private final MediaTypeDescriptor delegate;
  private final MediaType mt;

  public MediaTypeDescriptorImpl(MediaTypeDescriptor delegate, MediaType mt) {
    this.delegate = delegate;
    this.mt = mt;
  }

  @Override
  public String getMediaType() {
    return delegate.getMediaType();
  }

  @Override
  public DataTypeReference getDataType() {
    return delegate.getDataType();
  }

  @Override
  public String getSyntax() {
    return delegate.getSyntax();
  }

  @Override
  public float getQualityOfSourceFactor() {
    return mt.getQualityOfSource();
  }

  @Override
  public Map<String, String> getMediaTypeParams() {
    return mt.getParams();
  }
}
