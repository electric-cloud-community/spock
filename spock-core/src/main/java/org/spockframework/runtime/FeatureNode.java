package org.spockframework.runtime;

import org.spockframework.runtime.model.FeatureInfo;

import org.junit.platform.engine.*;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class FeatureNode extends SpockNode {
  protected final FeatureInfo featureInfo;

  public FeatureNode(UniqueId uniqueId, String displayName, TestSource source, FeatureInfo featureInfo) {
    super(uniqueId, displayName, source);
    this.featureInfo = featureInfo;
  }

  @Override
  public Type getType() {
    return Type.CONTAINER;
  }

  @Override
  public SkipResult shouldBeSkipped(SpockExecutionContext context) throws Exception {
    return shouldBeSkipped(featureInfo);
  }

  @Override
  public void around(SpockExecutionContext context, Invocation<SpockExecutionContext> invocation) {
    ErrorInfoCollector errorInfoCollector = new ErrorInfoCollector();
    final SpockExecutionContext innerContext = context.withErrorInfoCollector(errorInfoCollector);
    context.getRunner().runFeature(innerContext, () -> sneakyInvoke(invocation, innerContext));
    errorInfoCollector.assertEmpty();
  }

  @Override
  public boolean mayRegisterTests() {
    return featureInfo.isParameterized();
  }

  @Override
  public Set<TestTag> getTags() {
    return featureInfo.getTags().stream().map(tag->TestTag.create(tag.getName())).collect(Collectors.toSet());
  }
}
