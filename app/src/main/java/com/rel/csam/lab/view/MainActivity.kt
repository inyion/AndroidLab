package com.rel.csam.lab.view

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.rel.csam.lab.R
import com.rel.csam.lab.database.AppDatabase
import com.rel.csam.lab.database.Tag
import com.rel.csam.lab.database.Todo
import com.rel.csam.lab.databinding.ActivityMainBinding
import com.rel.csam.lab.view.expending.ExpandingItem
import com.rel.csam.lab.view.expending.ExpandingList
import com.rel.csam.lab.view.faboptions.FabOptions
import com.rel.csam.lab.viewmodel.CommonBindingComponent
import com.rel.csam.lab.viewmodel.TodoViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import android.os.Environment
import android.text.TextUtils
import androidx.annotation.ColorRes
import com.rel.csam.lab.database.TodoAndTag
import com.rel.csam.lab.viewmodel.TagModel
import io.reactivex.Observable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


/**
 * creator : sam
 * date : 2019. 1. 12.
 */
class MainActivity: ViewModelActivity<TodoViewModel>() {

    override fun createViewModel() {
        createViewModel(TodoViewModel::class.java)
    }

    override fun createDataBindingComponent() {
        createDataBindingComponent(CommonBindingComponent())
    }


    private lateinit var expandingList: ExpandingList
    private lateinit var fabOptions: FabOptions
    override fun onCreate() {
        val binding= setContentLayout<ActivityMainBinding>(R.layout.activity_main)
        binding.viewModel = viewModel
        viewModel.initDatabase(this@MainActivity)
        expandingList = binding.expandingListMain
        createItems()
        fabOptions = binding.fabOptions
        fabOptions.setOnClickListener{ v->
            onMenu(v)
        }

        initViewModel()
    }

    private var isFirst = false
    private fun createItems() {

        viewModel.addDisposable(viewModel.getTodoList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list ->

                    if(!isFirst) {// 라이브러리가 recycler가 아니라서 계속 타면 여러개가 됨
                        val map: HashMap<String, ArrayList<TodoAndTag>> = HashMap()
                        val groupTagList: ArrayList<String> = ArrayList()
                        if (list.isNotEmpty()) {
                            list.forEach { todoAndTag ->

                                var todoList = map[todoAndTag.todo.tag]
                                if (todoList == null) {
                                    todoList = ArrayList()
                                    groupTagList.add(todoAndTag.todo.tag)
                                }
                                todoList.add(todoAndTag)
                                map[todoAndTag.todo.tag] = todoList
                            }

                            for (group in groupTagList) {
                                addItem(group, map[group]!!, map[group]!![0].tag.color)
                            }
                        } else {
                            val tag = Tag("할일", "group", Color.WHITE)
                            viewModel.addDisposable(viewModel.insertTag(tag).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        addItem(tag.tagName, ArrayList(), tag.color)
                                    })
                        }
                        isFirst = true
                    }
                })


//        addItem("Mary", arrayOf("Dog", "Horse", "Boat"), R.color.blue, R.drawable.ic_ghost)
//        addItem("Ana", arrayOf("Cat"), R.color.purple, R.drawable.ic_ghost)
//        addItem("Peter", arrayOf("Parrot", "Elephant", "Coffee"), R.color.yellow, R.drawable.ic_ghost)
//        addItem("Joseph", arrayOf(), R.color.orange, R.drawable.ic_ghost)
//        addItem("Paul", arrayOf("Golf", "Football"), R.color.green, R.drawable.ic_ghost)
    }

    val tagMap: HashMap<String, ExpandingItem> = HashMap()
    private fun addItem(title: String, subItems: ArrayList<TodoAndTag>, color: Int) {
        //Let's create an item with R.layout.expanding_layout
        val item = expandingList.createNewItem(R.layout.expanding_layout)

        //If item creation is successful, let's configure it
        if (item != null) {
            item.setIndicatorColor(color)
            item.setIndicatorIconRes(R.drawable.ic_ghost)
            //It is possible to get any view inside the inflated layout. Let's set the text in the item
            (item.findViewById(R.id.title) as TextView).text = title
            tagMap[title] = item

            //We can create items in batch.
            item.createSubItems(subItems.size)
            for (i in 0 until item.subItemsCount) {
                //Let's get the created sub item by its index
                val view = item.getSubItemView(i)

                //Let's set some values in
                bindSubItem(item, view, subItems[i].todo)
            }
            (item.findViewById(R.id.add_more_sub_items) as View).setOnClickListener {v ->
                val text = EditText(v.context)
                val builder = AlertDialog.Builder(v.context)
                builder.setView(text)
                builder.setTitle(R.string.enter_title)
                builder.setPositiveButton(android.R.string.ok) { _, _ ->
                    val todo = Todo(null, text.text.toString(), title)
                    viewModel.addDisposable(viewModel.insertTodo(todo)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                val newSubItem = item.createSubItem()
                                bindSubItem(item, newSubItem!!, todo)
                            })

                }
                builder.setNegativeButton(android.R.string.cancel, null)
                builder.show()
            }

            item.setStateChangedListener { expanded ->
                if (expanded) {
                    (item.findViewById(R.id.remove_item) as View).visibility = View.VISIBLE
                } else {
                    (item.findViewById(R.id.remove_item) as View).visibility = View.GONE
                }

            }

            if (item.isExpanded) {
                (item.findViewById(R.id.remove_item) as View).visibility = View.VISIBLE
            } else {
                (item.findViewById(R.id.remove_item) as View).visibility = View.GONE
            }

            (item.findViewById(R.id.remove_item) as View).setOnClickListener {v ->

                val dialog = android.app.AlertDialog.Builder(v.context)
                val items = arrayOf<CharSequence>(getString(R.string.update),
                        getString(R.string.delete))

                dialog.setItems(items) { dialog, which ->
                    when(items[which]) {
                        getString(R.string.update) -> {
                            val text = EditText(v.context)
                            text.setText(title, TextView.BufferType.EDITABLE)
                            val builder = AlertDialog.Builder(v.context)
                            builder.setView(text)
                            builder.setTitle(R.string.enter_title)
                            builder.setPositiveButton(android.R.string.ok) { _, _ ->

                                viewModel.addDisposable(Observable.fromCallable {
                                            viewModel.tagModel.updateTag(title, text.text.toString())
                                        }
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe {
                                            (item.findViewById(R.id.title) as TextView).text = text.text.toString()
                                        })
                            }
                            builder.setNegativeButton(android.R.string.cancel, null)
                            builder.show()
                        }
                        getString(R.string.delete) -> {

                            viewModel.addDisposable(viewModel.deleteTag(title)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        expandingList.removeItem(item)
                                    })
                        }
                    }

                    dialog.dismiss()
                }

                dialog.show()
            }
        }
    }

    private fun bindSubItem(item: ExpandingItem?, view: View, todo: Todo) {
        (view.findViewById<View>(R.id.sub_title) as TextView).text = todo.name
        view.findViewById<View>(R.id.remove_sub_item).setOnClickListener {v ->

            val dialog = android.app.AlertDialog.Builder(v.context)
            val items = arrayOf<CharSequence>(getString(R.string.update),
                    getString(R.string.delete))

            dialog.setItems(items) { dialog, which ->
                when(items[which]) {
                    getString(R.string.update) -> {
                        val text = EditText(v.context)
                        text.setText(todo.name, TextView.BufferType.EDITABLE)
                        val builder = AlertDialog.Builder(v.context)
                        builder.setView(text)
                        builder.setTitle(R.string.enter_title)
                        builder.setPositiveButton(android.R.string.ok) { _, _ ->
                            todo.name = text.text.toString()
                            viewModel.addDisposable(viewModel.updateTodo(todo)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        (view.findViewById<View>(R.id.sub_title) as TextView).text = todo.name
                                    })

                        }
                        builder.setNegativeButton(android.R.string.cancel, null)
                        builder.show()
                    }
                    getString(R.string.delete) -> {

                        viewModel.addDisposable(viewModel.deleteTodo(todo)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    item!!.removeSubItem(view)
                                })
                    }
                }

                dialog.dismiss()
            }

            dialog.show()

        }
        view.findViewById<View>(R.id.sub_title).setOnClickListener { v ->

            val dialog = android.app.AlertDialog.Builder(v.context)
            val items = arrayOf<CharSequence>(getString(R.string.search),
                    getString(R.string.search_image),
                    getString(R.string.search_youtube),
                    getString(R.string.search_shopping))
            dialog.setItems(items) { dialog, which ->
                when(items[which]) {
                    getString(R.string.search) -> {
                        val intent = Intent(v.context, WebViewActivity::class.java)
                        intent.putExtra("url", "https://www.google.com/search?q=" + todo.name.replace(" ", "+"))
                        startActivity(intent)
                    }
                    getString(R.string.search_image) -> {

                        val uri = Uri.parse("https://www.instagram.com/explore/tags/"+ todo.name.replace(" ", "") +"/?hl=ko")
                        v.context.startActivity(Intent(Intent.ACTION_VIEW, uri))
//                        val intent = Intent(v.context, WebImageListActivity::class.java)
//                        intent.putExtra("keyword", todo.name)
//                        startActivity(intent)
                    }
                    getString(R.string.search_youtube) -> {

                        val uri = Uri.parse("https://www.youtube.com/results?search_query=" + todo.name.replace(" ", "+"))
                        startActivity(Intent(Intent.ACTION_VIEW, uri))

                    }
                    getString(R.string.search_shopping) -> {
                        //TODO 네이버, 카카오 추가
                        val intent = Intent(v.context, WebViewActivity::class.java)
                        intent.putExtra("url", "https://www.google.com/search?q=" + todo.name.replace(" ", "+") + "&tbm=shop")
                        startActivity(intent)
                    }
                }

                dialog.dismiss()
            }

            dialog.show()
        }
    }

    fun onMenu(v: View) {
        when(v.id) {
            R.id.faboptions_textsms -> {
//                fabOptions.setButtonColor(R.id.faboptions_textsms, R.color.orange)
//                fabOptions.setButtonColor(R.id.faboptions_share, R.color.white)
                startActivityForResult(Intent(v.context, CanvasActivity::class.java), requestCodeCanvas)
            }
            R.id.faboptions_share -> {
//                fabOptions.setButtonColor(R.id.faboptions_textsms, R.color.white)
//                fabOptions.setButtonColor(R.id.faboptions_share, R.color.orange)
                Toast.makeText(v.context, "Share", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestCodeCanvas && data != null) {

            if (data.hasExtra("tag")) {
                val text = data.getStringExtra("tag")
                if (!TextUtils.isEmpty(text)) {
                    val tag = Tag(text, "group", data.getIntExtra("tag_color", Color.WHITE))
                    viewModel.addDisposable(viewModel.insertTag(tag)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                addItem(tag.tagName, ArrayList(), tag.color)
                            })
                }
            } else {
                val text = data.getStringExtra("memo")
                val tagSpan = data.getStringExtra("tag_list").split(",")

                if (TextUtils.isEmpty(text)) return

                val todoList = ArrayList<Todo>()
                for (tagName in tagSpan) {
                    todoList.add(Todo(null, text, tagName))
                }

                viewModel.addDisposable(viewModel.insertTodoList(todoList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe{

                            for (todo in todoList) {
                                val tagItem = tagMap[todo.tag]
                                if (tagItem != null) {
                                    val newSubItem = tagItem.createSubItem()
                                    bindSubItem(tagItem, newSubItem!!, todo)
                                } else {
                                    val todoArr: ArrayList<TodoAndTag> = ArrayList()
                                    val todoAndTag = TodoAndTag()
                                    val tag = Tag(todo.tag, "group", Color.WHITE)
                                    todoAndTag.todo = todo
                                    todoAndTag.tag = tag
                                    todoArr.add(todoAndTag)
                                    addItem(todo.tag, todoArr, tag.color)
                                }
                            }
                        })
            }
        }

    }

    companion object {

        // Used to load the 'native-lib' library on application startup.
//        init {
//            System.loadLibrary("native-lib")
//        }

        val requestCodeCanvas = 2
    }
}
