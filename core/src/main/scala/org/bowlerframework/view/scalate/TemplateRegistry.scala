package org.bowlerframework.view.scalate

import selectors.{TemplateSuffixSelector, LayoutSelector}
import util.matching.Regex
import collection.mutable.{MutableList, HashMap}
import reflect.BeanProperty
import org.bowlerframework.{MappedPath, Request, HTTP}

/**
 * Retrieves a Template based on a request and it's contents, headers and/or path.
 * the order of layouts and templates added matters, as the selectors are held in order and the first match will return a result. <br/>
 * Also holds Scalate configurations, such as TemplateResolver, preference order of Scalate template types (ssp, jade, mustache etc), root packages/folders for views and layouts etc.
 */
object TemplateRegistry{

  @BeanProperty
  var templateTypePreference = List(".mustache", ".ssp", ".jade", ".scaml")

  @BeanProperty
  var templateResolver: TemplateResolver = new ClasspathTemplateResolver

  @BeanProperty
  var rootViewPackageOrFolder = "/views"

  @BeanProperty
  var rootLayoutPackageOrFolder = "/layouts"

  private var suffixSelectors = new MutableList[TemplateSuffixSelector]()

  private var layoutSelectors = new MutableList[LayoutSelector]()

  def appendLayoutSelectors(selectors: List[_ <: LayoutSelector]) = selectors.foreach(f => {layoutSelectors += f})

  def appendLayoutSelector(selector: LayoutSelector) = {layoutSelectors += selector}

  def appendSuffixSelectors(selectors: List[_ <: TemplateSuffixSelector]) = selectors.foreach(f => {suffixSelectors += f})

  def appendSuffixSelector(selector: TemplateSuffixSelector) = {suffixSelectors += selector}

  def reset = {
    layoutSelectors = new MutableList[LayoutSelector]()
    suffixSelectors = new MutableList[TemplateSuffixSelector]()
  }

  def getLayout(request: Request): Option[Layout] = {
    val selector = layoutSelectors.find(p => {p.find(request) != None})
    if(selector == None) return None
    else return selector.get.find(request)
  }

  def getSuffixes(request: Request): List[String] = suffixSelectors.filter(p => {p.find(request) != None}).map(f => {f.find(request).get}).toList

}