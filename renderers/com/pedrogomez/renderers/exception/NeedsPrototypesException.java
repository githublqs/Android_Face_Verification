/*
 * Copyright (C) 2014 Pedro Vicente Gómez Sánchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pedrogomez.renderers.exception;

/**
 * Exception created to be thrown when a RendererBuilder be created or configured without any
 * prototype. A RendererBuilder implementation needs prototypes to create or recycle new Renderer
 * instances.
 *
 * @author Pedro Vicente Gómez Sánchez.
 */
public class NeedsPrototypesException extends RendererException {

  public NeedsPrototypesException(String detailMessage) {
    super(detailMessage);
  }
}
