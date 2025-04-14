<script setup lang="ts">
import {computed, ref} from 'vue'
import {useMutation, useQuery, useQueryClient} from "@tanstack/vue-query";
import {Button} from "@/components/ui/button";
import {ArrowLeftIcon, ArrowRightIcon} from "@radix-icons/vue";
import {Input} from "@/components/ui/input";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'

interface Book {
  id?: string
  title: string
  isAvailable?: boolean
  author?: Author | null
  authorId: string
  pages: number
}

interface Author {
  id: string
  firstName: string
  lastName: string
  birthDate: string | Date
}

interface BookResponse {
  content: Book[]
  totalPages: number
}

interface AuthorResponse {
  content: Author[]
  totalPages: number
}

interface Reader {
  id: string
  firstName: string
  lastName: string
}

interface ReaderResponse {
  content: Author[]
  totalPages: number
}

interface Rental {
  id: string
  book: Book
  reader: Reader
  rentalDate: string | Date
  returnDate: string | Date
}

interface RentalResponse {
  content: Rental[]
  totalPages: number
}

const queryClient = useQueryClient();

const page = ref(0);
const totalPages = ref(1);
const itemsPerPage = ref(3);
const bookId = ref(null);
const readerId = ref(null);


const fetchBooks = async ({ queryKey }: { queryKey: (string | number)[]
}) => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/books`);
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  const data = await response.json() as BookResponse;
  totalPages.value = data.totalPages;
  console.log(totalPages.value)
  return data;
};

const {data: dataBooks, error: errorBooks, isLoading: isLoadingBooks, isError: isErrorBooks} = useQuery({
  queryKey: ["books"],
  queryFn: fetchBooks,
  keepPreviousData: true,
})

const books = computed(() => dataBooks.value ? dataBooks.value.content : [])



const fetchReaders = async ({queryKey}: {
  queryKey: (string | number)[]
}) => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/readers`);
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  const data = await response.json() as ReaderResponse;
  totalPages.value = data.totalPages;
  console.log(totalPages.value)
  return data;
};

const {data: dataReaders,error: errorReaders, isLoading: isLoadingReaders, isError: isErrorReaders} = useQuery({
  queryKey: ["readers"],
  queryFn: fetchReaders,
  keepPreviousData: true,
})


const readers = computed(() => dataReaders.value ? dataReaders.value.content : [])

const queryKey = computed(() => {
  return ['rentals', page.value, itemsPerPage.value];
});

const fetchRentals = async ({queryKey}: {
  queryKey: (string | number)[]
}) => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/rentals?page=${page.value}&size=${itemsPerPage.value}`);
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  const data = await response.json() as RentalResponse;
  totalPages.value = data.totalPages;
  console.log(totalPages.value)
  return data;
};

const {data, error, isLoading, isError} = useQuery({
  queryKey: queryKey.value,
  queryFn: fetchRentals,
  keepPreviousData: true,
})


const rentals = computed(() => data.value ? data.value.content : [])


const updateMutation = useMutation({
  mutationFn: async ({bookId, readerId}: { bookId: string, readerId: string }) => {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/rentals/rent?bookId=${bookId}&readerId=${readerId}`, {
      method: "POST",
      headers: {
        'Content-Type': 'application/json',
      },
    });
    if (!response.ok) {
      const errorResponse = await response.text();
      console.log(errorResponse)
      throw new Error(errorResponse || 'Network response was not ok');
    }
    return response.json() as Promise<Book>;
  },
  onSuccess: () => {
    queryClient.invalidateQueries({queryKey: queryKey.value});
  },
  onError: (error: Error) => {
    alert(error.message);
  },
});

const retMutation = useMutation({
  mutationFn: async ({id}: { id: string }) => {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/rentals/return/${id}`, {
      method: "POST",
      headers: {
        'Content-Type': 'application/json',
      },
    });
    if (!response.ok) {
      const errorResponse = await response.text();
      console.log(errorResponse)
      throw new Error(errorResponse || 'Network response was not ok');
    }
    return response.json() as Promise<Book>;
  },
  onSuccess: () => {
    queryClient.invalidateQueries({queryKey: queryKey.value});
  },
  onError: (error: Error) => {
    alert(error.message);
  },
});

const returnBook = async (id: string) => {
  await retMutation.mutateAsync({id});
};


const nextPage = () => {
  if (page.value < totalPages.value - 1) {
    page.value++;
  }
  queryClient.invalidateQueries({queryKey: queryKey.value});
};

const prevPage = () => {
  if (page.value > 0) {
    page.value--;
  }
  queryClient.invalidateQueries({queryKey: ['books']});
};


const saveRental = async () => {
  if (!bookId.value || !readerId.value) {
    alert("Please fill in all fields");
    return;
  }
  await updateMutation.mutateAsync(
      {
        bookId: bookId.value,
        readerId: readerId.value
      });
  bookId.value = null;
  readerId.value = null;
};


</script>


<template>
  <div class="flex items-center h-screen flex-col gap-10 p-4">
    <div class="flex flex-row w-full - justify-between">
      <h1 class="font-bold text-xl">Rentals</h1>
      <Dialog>
        <DialogTrigger as-child>
          <Button>Add</Button>
        </DialogTrigger>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Add a new book</DialogTitle>
          </DialogHeader>
          <Card class="sm:p-4 p-1">
            <CardContent class="flex flex-col gap-4">
              <div class="justify-start flex flex-col">
                <label for="bookId" class="text-sm text-gray-600">Book</label>
                <div>
                  <select
                      v-model="bookId"
                      id="bookId"
                      class="border border-gray-300 rounded-md p-2"
                  >
                    <option disabled value="">Select book</option>
                    <option v-for="book in books" :key="book.id" :value="book.id">
                      {{ book.title }}
                    </option>
                  </select>
                </div>
                <label for="readrId" class="text-sm text-gray-600">Reader</label>
                <div>
                  <select
                      v-model="readerId"
                      id="readrId"
                      class="border border-gray-300 rounded-md p-2"
                  >
                    <option disabled value="">Select reader</option>
                    <option v-for="reader in readers" :key="reader.id" :value="reader.id">
                      {{ reader.firstName }} {{ reader.lastName }}
                    </option>
                  </select>
                </div>
                <Button
                    class="mt-2"
                    @click="saveRental(true)"
                >
                  Save
                </Button>
                <DialogClose as-child>
                  <Button
                      variant="outline"
                      class="mt-2"
                      @click="cancelEditing"
                  >
                    Cancel
                  </Button>
                </DialogClose>
              </div>
            </CardContent>
          </Card>
        </DialogContent>
      </Dialog>
    </div>

    <div v-if="isLoading || isLoadingBooks || isLoadingReaders">
      Loading...
    </div>

    <div v-else-if="isError || isErrorBooks || isErrorReaders">
      Error: {{ error.message }} {{ errorBooks.message }} {{errorReaders.message }}
    </div>

    <div v-else>
      <ul>
        <li v-for="rental in rentals" :key="rental.id">
          <div class="p-10">
            <Card class="sm:p-4 p-1">
              <CardContent class="flex flex-col gap-4">
                <div class="justify-start flex flex-col">
                  <label for="title" class="text-sm text-gray-600">Title</label>
                  <Input
                      v-model="rental.book.title"
                      type="text"
                      id="title"
                      :disabled="true"
                  />
                </div>
                <div class="justify-start flex flex-col">
                  <label for="reader" class="text-sm text-gray-600">Reader</label>
                  <Input
                      v-model="rental.reader.lastName"
                      type="text"
                      id="reader"
                      :disabled="true"
                  />
                </div>
                <div class="justify-start flex flex-col">
                  <label for="rentDate" class="text-sm text-gray-600">Rental Date</label>
                  <Input
                      v-model="rental.rentalDate"
                      type="date"
                      id="rentDate"
                      :disabled="true"
                  />
                </div>
                <div class="justify-start flex flex-col">
                  <label for="returnDate" class="text-sm text-gray-600">Return Date</label>
                  <Input
                      v-model="rental.returnDate"
                      type="date"
                      id="returnDate"
                      :disabled="true"
                  />
                </div>
                <div class="justify-start flex flex-col">
                  <Button
                      v-if="rental.returnDate === null"
                      class="mt-2"
                      @click="returnBook(rental.id)"
                  >
                    Return
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        </li>
      </ul>

    </div>

    <div class="flex flex-row gap-4 items-center p-4">
      <Button variant="outline" @click="prevPage">
        <ArrowLeftIcon/>
      </Button>
      <p>
        {{ page + 1 }}
      </p>
      <Button variant="outline" @click="nextPage">
        <ArrowRightIcon/>
      </Button>
    </div>


  </div>

</template>

<style scoped>

</style>